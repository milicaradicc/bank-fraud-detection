package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.enums.ActionType;
import com.ftn.sbnz.model.enums.RiskLevel;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.model.facts.Client;
import com.ftn.sbnz.model.facts.Flag;
import com.ftn.sbnz.model.facts.RiskScore;
import com.ftn.sbnz.model.entities.User;
import com.ftn.sbnz.service.dto.PendingTransaction;
import com.ftn.sbnz.service.dto.TransactionRequest;
import com.ftn.sbnz.service.dto.TransactionResponse;
import com.ftn.sbnz.service.repository.UserRepository;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final KieSession kieSession;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private long txCounter = 500;
    private final List<Transaction> transactionLog = new ArrayList<>();
    private final Map<String, PendingTransaction> pendingTransactions = new HashMap<>();

    public TransactionService(KieSession kieSession,
                              EmailService emailService,
                              UserRepository userRepository) {
        this.kieSession = kieSession;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    public List<Transaction> getAllTransactions() {
        return kieSession.getObjects(o -> o instanceof Transaction)
                .stream()
                .map(o -> (Transaction) o)
                .sorted(Comparator.comparing(Transaction::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByClient(String clientId) {
        return kieSession.getObjects(o -> o instanceof Transaction)
                .stream()
                .map(o -> (Transaction) o)
                .filter(t -> clientId.equals(t.getClientId()))
                .sorted(Comparator.comparing(Transaction::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public void addToLog(Transaction tx) {
        transactionLog.add(tx);
    }

    public TransactionResponse processTransaction(TransactionRequest req) {
        String txId = "TX-" + String.format("%05d", ++txCounter);

        Transaction tx = new Transaction(txId, req.getClientId(),
                req.getAmount(), new Date(), req.getChannel());
        tx.setCurrency(req.getCurrency() != null ? req.getCurrency() : "EUR");
        tx.setCountry(req.getCountry());
        tx.setCity(req.getCity());
        tx.setDeviceId(req.getDeviceId());
        tx.setRecipientId(req.getRecipientId());
        tx.setRecipientCountry(req.getRecipientCountry());
        tx.setInflow(false);
        tx.setMerchantCategory(req.getMerchantCategory());
        tx.setMccCode(req.getMccCode());
        tx.setLatitude(req.getLatitude());
        tx.setLongitude(req.getLongitude());

        if (req.getRecipientId() != null && !req.getRecipientId().isBlank()) {
            Client client = kieSession.getObjects(o -> o instanceof Client)
                    .stream().map(o -> (Client) o)
                    .filter(c -> c.getClientId().equals(req.getClientId()))
                    .findFirst()
                    .orElse(null);

            boolean isNew = client == null || !client.isKnownRecipient(req.getRecipientId());
            tx.setRecipientIsNew(isNew);

            boolean isForeign = req.getRecipientCountry() != null
                    && !req.getRecipientCountry().isBlank()
                    && !req.getRecipientCountry().equals(req.getCountry());
            tx.setRecipientIsForeignAccount(isForeign);
        }

        transactionLog.add(tx);
        kieSession.insert(tx);
        kieSession.fireAllRules();

        Date txTime = tx.getTimestamp();

        List<Flag> flags = kieSession.getObjects(o -> o instanceof Flag)
                .stream().map(o -> (Flag) o)
                .filter(f -> {
                    if (txId.equals(f.getTransactionId())) return true;
                    if (req.getClientId().equals(f.getClientId())
                            && f.getTransactionId() == null
                            && f.getTimestamp() != null
                            && !f.getTimestamp().before(txTime)) return true;
                    return false;
                })
                .toList();

        Optional<RiskScore> riskScore = kieSession.getObjects(o -> o instanceof RiskScore)
                .stream().map(o -> (RiskScore) o)
                .filter(rs -> txId.equals(rs.getTransactionId()))
                .findFirst();

        Optional<Alert> alert = kieSession.getObjects(o -> o instanceof Alert)
                .stream().map(o -> (Alert) o)
                .filter(a -> txId.equals(a.getTransactionId()))
                .findFirst();

        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(txId);
        response.setClientId(req.getClientId());
        response.setAmount(req.getAmount());

        riskScore.ifPresentOrElse(rs -> {
            response.setRiskScore(rs.getScore());
            response.setRiskLevel(rs.getLevel());
            response.setContributingFlags(rs.getContributingFlags());
            response.setMultiplierReason(rs.getMultiplierReason());
        }, () -> {
            response.setRiskScore(0);
            response.setRiskLevel(RiskLevel.LOW);
            response.setContributingFlags(List.of());
        });

        alert.ifPresentOrElse(a -> {
            response.setAction(a.getAction().name());
            response.setMessage(a.getMessage());

            // Step-up — pošalji email sa kodom
            if (a.getAction() == ActionType.BLOCK_STEPUP) {
                sendStepUpCode(req.getClientId(), txId, req.getAmount());
                response.setRequiresStepUp(true);
            }
        }, () -> {
            response.setAction("LOG_ONLY");
            response.setMessage("Transakcija prošla bez upozorenja");
        });

        response.setTriggeredFlags(flags.stream()
                .map(f -> f.getType().name())
                .distinct()
                .collect(Collectors.toList()));

        return response;
    }

    private void sendStepUpCode(String clientId, String txId, double amount) {
        User user = userRepository.findAll().stream()
                .filter(u -> clientId.equals(u.getLinkedClientId()))
                .findFirst()
                .orElse(null);

        if (user == null || user.getEmail() == null) return;

        String code = String.format("%06d", new Random().nextInt(1_000_000));
        long expiresAt = System.currentTimeMillis() + (10 * 60 * 1000); // 10 min

        pendingTransactions.put(txId, new PendingTransaction(txId, code, expiresAt));
        emailService.sendVerificationCode(user.getEmail(), code, amount, txId);
    }

    public boolean confirmStepUp(String txId, String enteredCode) {
        PendingTransaction pending = pendingTransactions.get(txId);
        if (pending == null) return false;

        if (System.currentTimeMillis() > pending.getExpiresAt()) {
            pendingTransactions.remove(txId);
            return false;
        }

        pending.incrementAttempts();
        if (pending.getAttempts() > 3) {
            pendingTransactions.remove(txId);
            return false;
        }

        boolean matches = pending.getCode().equals(enteredCode);
        if (matches) {
            pendingTransactions.remove(txId);
        }
        return matches;
    }
}