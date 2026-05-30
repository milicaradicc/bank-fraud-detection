package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.enums.ActionType;
import com.ftn.sbnz.model.enums.RiskLevel;
import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.service.dto.StatsResponse;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final KieSession kieSession;

    public StatsService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public StatsResponse getStats() {
        List<Transaction> transactions = kieSession.getObjects(o -> o instanceof Transaction)
                .stream().map(o -> (Transaction) o).toList();

        List<Alert> alerts = kieSession.getObjects(o -> o instanceof Alert)
                .stream().map(o -> (Alert) o).toList();

        StatsResponse stats = new StatsResponse();

        stats.setTotalTransactions(transactions.size());

        stats.setBlockedToday(alerts.stream()
                .filter(a -> a.getAction() == ActionType.BLOCK
                        || a.getAction() == ActionType.FREEZE_ACCOUNT
                        || a.getAction() == ActionType.BLOCK_STEPUP)
                .count());

        stats.setActiveAlerts(alerts.stream()
                .filter(Alert::isEscalatedToAnalyst)
                .count());

        stats.setCriticalAlerts(alerts.stream()
                .filter(a -> a.getRiskLevel() == RiskLevel.CRITICAL)
                .count());

        // Distribucija po action tipu
        Map<String, Long> byAction = alerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAction().name(), Collectors.counting()
                ));

        stats.setFraudByType(List.of(
                new StatsResponse.FraudTypeCount("Blokada",
                        byAction.getOrDefault("BLOCK", 0L) +
                                byAction.getOrDefault("BLOCK_STEPUP", 0L), "#ef4444"),
                new StatsResponse.FraudTypeCount("Zamrzavanje",
                        byAction.getOrDefault("FREEZE_ACCOUNT", 0L), "#f59e0b"),
                new StatsResponse.FraudTypeCount("Alert analitičaru",
                        byAction.getOrDefault("ALERT_ANALYST", 0L), "#8b5cf6"),
                new StatsResponse.FraudTypeCount("AML prijava",
                        byAction.getOrDefault("AML_REPORT", 0L), "#2d7dd2")
        ));

        // Distribucija po risk nivou
        Map<RiskLevel, Long> byRisk = alerts.stream()
                .collect(Collectors.groupingBy(Alert::getRiskLevel, Collectors.counting()));

        stats.setRiskDistribution(List.of(
                new StatsResponse.RiskDistribution("Nizak",
                        byRisk.getOrDefault(RiskLevel.LOW, 0L), "#22c55e"),
                new StatsResponse.RiskDistribution("Srednji",
                        byRisk.getOrDefault(RiskLevel.MEDIUM, 0L), "#f59e0b"),
                new StatsResponse.RiskDistribution("Visok",
                        byRisk.getOrDefault(RiskLevel.HIGH, 0L), "#ef4444"),
                new StatsResponse.RiskDistribution("Kritičan",
                        byRisk.getOrDefault(RiskLevel.CRITICAL, 0L), "#dc2626")
        ));

        return stats;
    }
}