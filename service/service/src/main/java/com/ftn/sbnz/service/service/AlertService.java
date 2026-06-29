package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.model.facts.Client;
import com.ftn.sbnz.model.facts.Flag;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AlertService {

    private final KieSession kieSession;

    public AlertService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public List<Alert> getAllAlerts() {
        Collection<?> objects = kieSession.getObjects(
                obj -> obj instanceof Alert
        );
        return new ArrayList<>((Collection<Alert>) (Collection<?>) objects);
    }

    public List<Alert> getAlertsByClient(String clientId) {
        return getAllAlerts().stream()
                .filter(a -> a.getClientId().equals(clientId))
                .toList();
    }

    public Alert confirmAlert(String transactionId) {
        Alert alert = findAlert(transactionId);
        if (alert == null) return null;

        FactHandle handle = kieSession.getFactHandle(alert);
        alert.setStatus("confirmed");
        if (handle != null) {
            kieSession.update(handle, alert);
        }

        Client client = findClient(alert.getClientId());
        if (client != null) {
            FactHandle ch = kieSession.getFactHandle(client);
            client.setOnWatchlist(true);
            if (ch != null) {
                kieSession.update(ch, client);
            }
        }

        System.out.println("[ANALITICAR] Potvrdjena prevara: " + alert);
        return alert;
    }

    public Alert dismissAlert(String transactionId) {
        Alert alert = findAlert(transactionId);
        if (alert == null) return null;

        List<FactHandle> toDelete = new ArrayList<>(
                kieSession.getFactHandles(
                        o -> o instanceof Flag
                                && transactionId.equals(((Flag) o).getTransactionId()))
        );
        for (FactHandle fh : toDelete) {
            kieSession.delete(fh);
        }

        FactHandle handle = kieSession.getFactHandle(alert);
        alert.setStatus("dismissed");
        if (handle != null) {
            kieSession.update(handle, alert);
        }

        Client client = findClient(alert.getClientId());
        if (client != null) {
            FactHandle ch = kieSession.getFactHandle(client);
            client.setOnWatchlist(false);
            if (ch != null) {
                kieSession.update(ch, client);
            }
        }

        System.out.println("[ANALITICAR] Odbacen alert " + transactionId
                + " (lazni alarm) - uklonjeno " + toDelete.size()
                + " flagova, klijent " + alert.getClientId() + " skinut sa watchlist.");
        return alert;
    }

    private Alert findAlert(String transactionId) {
        return getAllAlerts().stream()
                .filter(a -> transactionId.equals(a.getTransactionId()))
                .findFirst()
                .orElse(null);
    }

    private Client findClient(String clientId) {
        Collection<?> clients = kieSession.getObjects(o -> o instanceof Client);
        for (Object o : clients) {
            Client c = (Client) o;
            if (c.getClientId().equals(clientId)) return c;
        }
        return null;
    }

    public List<String> getClientFlags(String clientId) {
        return kieSession.getObjects(o -> o instanceof Flag)
                .stream().map(o -> (Flag) o)
                .filter(f -> clientId.equals(f.getClientId()))
                .map(f -> f.getType().name())
                .distinct()
                .toList();
    }
}