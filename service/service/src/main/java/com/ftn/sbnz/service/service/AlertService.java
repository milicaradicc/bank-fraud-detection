package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.facts.Alert;
import org.kie.api.runtime.KieSession;
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
}