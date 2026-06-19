package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.entities.ClientUser;
import com.ftn.sbnz.model.entities.User;
import com.ftn.sbnz.model.enums.Role;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.service.dto.TransactionRequest;
import com.ftn.sbnz.service.dto.TransactionResponse;
import com.ftn.sbnz.service.repository.ClientUserRepository;
import com.ftn.sbnz.service.repository.UserRepository;
import com.ftn.sbnz.service.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final ClientUserRepository clientUserRepository;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, ClientUserRepository clientUserRepository, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.clientUserRepository = clientUserRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/my")
    public List<Transaction> getMyTransactions(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        String clientId = user.getLinkedClientId();
        if (clientId == null) return List.of();

        return transactionService.getTransactionsByClient(clientId);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> processTransaction(
            @RequestBody TransactionRequest request,
            Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        if (user.getRole() == Role.CLIENT) {
            request.setClientId(user.getLinkedClientId());
        }

        TransactionResponse response = transactionService.processTransaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{txId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmTransaction(
            @PathVariable String txId,
            @RequestBody Map<String, String> body) {
        String code = body.get("code");
        boolean confirmed = transactionService.confirmStepUp(txId, code);

        if (confirmed) {
            return ResponseEntity.ok(Map.of("confirmed", true, "message", "Transakcija potvrđena i puštena."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("confirmed", false, "message", "Pogrešan ili istekao kod."));
        }
    }
}