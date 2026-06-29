package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.service.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/client/{clientId}")
    public List<Alert> getByClient(@PathVariable String clientId) {
        return alertService.getAlertsByClient(clientId);
    }

    @PostMapping("/{transactionId}/confirm")
    public ResponseEntity<Alert> confirm(@PathVariable String transactionId) {
        Alert alert = alertService.confirmAlert(transactionId);
        if (alert == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/{transactionId}/dismiss")
    public ResponseEntity<Alert> dismiss(@PathVariable String transactionId) {
        Alert alert = alertService.dismissAlert(transactionId);
        if (alert == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/flags/{clientId}")
    public List<String> getClientFlags(@PathVariable String clientId) {
        return alertService.getClientFlags(clientId);
    }
}