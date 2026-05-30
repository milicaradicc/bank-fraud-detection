package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.events.Transaction;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class TransactionService {

    private final KieSession kieSession;

    public TransactionService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public List<Transaction> getAllTransactions() {
        Collection<?> objects = kieSession.getObjects(
                obj -> obj instanceof Transaction
        );
        return new ArrayList<>((Collection<Transaction>) (Collection<?>) objects);
    }

    public List<Transaction> getTransactionsByClient(String clientId) {
        return getAllTransactions().stream()
                .filter(t -> t.getClientId().equals(clientId))
                .toList();
    }
}