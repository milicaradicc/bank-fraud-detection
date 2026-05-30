package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.service.dto.TransactionRequest;
import com.ftn.sbnz.service.dto.TransactionResponse;
import com.ftn.sbnz.service.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/client/{clientId}")
    public List<Transaction> getByClient(@PathVariable String clientId) {
        return transactionService.getTransactionsByClient(clientId);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> processTransaction(
            @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.processTransaction(request);
        return ResponseEntity.ok(response);
    }
}