package com.ftn.sbnz.service.dto;

import com.ftn.sbnz.model.enums.RiskLevel;
import java.util.List;

public class TransactionResponse {
    private String transactionId;
    private String clientId;
    private double amount;
    private double riskScore;
    private RiskLevel riskLevel;
    private String action;
    private String message;
    private List<String> triggeredFlags;
    private List<String> contributingFlags;
    private String multiplierReason;

    public TransactionResponse() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String v) { this.transactionId = v; }
    public String getClientId() { return clientId; }
    public void setClientId(String v) { this.clientId = v; }
    public double getAmount() { return amount; }
    public void setAmount(double v) { this.amount = v; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double v) { this.riskScore = v; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel v) { this.riskLevel = v; }
    public String getAction() { return action; }
    public void setAction(String v) { this.action = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
    public List<String> getTriggeredFlags() { return triggeredFlags; }
    public void setTriggeredFlags(List<String> v) { this.triggeredFlags = v; }
    public List<String> getContributingFlags() { return contributingFlags; }
    public void setContributingFlags(List<String> v) { this.contributingFlags = v; }
    public String getMultiplierReason() { return multiplierReason; }
    public void setMultiplierReason(String v) { this.multiplierReason = v; }
}