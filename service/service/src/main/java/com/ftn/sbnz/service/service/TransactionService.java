package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.enums.RiskLevel;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.model.facts.Flag;
import com.ftn.sbnz.model.facts.RiskScore;
import com.ftn.sbnz.service.dto.TransactionRequest;
import com.ftn.sbnz.service.dto.TransactionResponse;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final KieSession kieSession;
    private long txCounter = 500;

    public TransactionService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public List<Transaction> getAllTransactions() {
        return kieSession.getObjects(o -> o instanceof Transaction)
                .stream().map(o -> (Transaction) o).toList();
    }

    public List<Transaction> getTransactionsByClient(String clientId) {
        return getAllTransactions().stream()
                .filter(t -> t.getClientId().equals(clientId))
                .toList();
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
        tx.setRecipientIsNew(req.isRecipientIsNew());
        tx.setRecipientIsForeignAccount(req.isRecipientIsForeignAccount());
        tx.setRecipientCountry(req.getRecipientCountry());
        tx.setInflow(req.isInflow());
        tx.setMerchantCategory(req.getMerchantCategory());
        tx.setMccCode(req.getMccCode());
        tx.setLatitude(req.getLatitude());
        tx.setLongitude(req.getLongitude());

        // ubaci u working memory i pokreni pravila
        kieSession.insert(tx);
        kieSession.fireAllRules();

        List<Flag> flags = kieSession.getObjects(o -> o instanceof Flag)
                .stream().map(o -> (Flag) o)
                .filter(f -> txId.equals(f.getTransactionId())
                        || req.getClientId().equals(f.getClientId()))
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
}