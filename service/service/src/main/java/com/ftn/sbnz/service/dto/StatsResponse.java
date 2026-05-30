package com.ftn.sbnz.service.dto;

import java.util.List;

public class StatsResponse {

    private int totalTransactions;
    private long blockedToday;
    private long activeAlerts;
    private long criticalAlerts;
    private List<FraudTypeCount> fraudByType;
    private List<RiskDistribution> riskDistribution;

    public record FraudTypeCount(String type, long count, String color) {}
    public record RiskDistribution(String level, long count, String color) {}

    public StatsResponse() {}

    public int getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(int v) { this.totalTransactions = v; }
    public long getBlockedToday() { return blockedToday; }
    public void setBlockedToday(long v) { this.blockedToday = v; }
    public long getActiveAlerts() { return activeAlerts; }
    public void setActiveAlerts(long v) { this.activeAlerts = v; }
    public long getCriticalAlerts() { return criticalAlerts; }
    public void setCriticalAlerts(long v) { this.criticalAlerts = v; }
    public List<FraudTypeCount> getFraudByType() { return fraudByType; }
    public void setFraudByType(List<FraudTypeCount> v) { this.fraudByType = v; }
    public List<RiskDistribution> getRiskDistribution() { return riskDistribution; }
    public void setRiskDistribution(List<RiskDistribution> v) { this.riskDistribution = v; }
}