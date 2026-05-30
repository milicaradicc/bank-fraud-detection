package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.facts.Client;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ClientService {

    private final KieSession kieSession;

    public ClientService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public List<Client> getAllClients() {
        Collection<? extends Object> objects = kieSession.getObjects(
                obj -> obj instanceof Client
        );
        return new ArrayList<>((Collection<Client>) (Collection<?>) objects);
    }

    public Client getClientById(String clientId) {
        return getAllClients().stream()
                .filter(c -> c.getClientId().equals(clientId))
                .findFirst()
                .orElse(null);
    }
}