package com.ftn.sbnz.model.facts;

import com.ftn.sbnz.model.enums.ActionType;
import com.ftn.sbnz.model.enums.RiskLevel;

import java.util.Date;

public class Alert {

    private String clientId;
    private String transactionId;
    private ActionType action;
    private RiskLevel riskLevel;
    private double score;
    private Date timestamp;
    private String message;
    private boolean escalatedToAnalyst;

    public Alert() {}

    public Alert(String clientId, String transactionId, ActionType action,
                 RiskLevel riskLevel, double score, Date timestamp, String message) {
        this.clientId = clientId;
        this.transactionId = transactionId;
        this.action = action;
        this.riskLevel = riskLevel;
        this.score = score;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public ActionType getAction() { return action; }
    public void setAction(ActionType action) { this.action = action; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isEscalatedToAnalyst() { return escalatedToAnalyst; }
    public void setEscalatedToAnalyst(boolean escalatedToAnalyst) { this.escalatedToAnalyst = escalatedToAnalyst; }

    @Override
    public String toString() {
        return "Alert{client=" + clientId + ", action=" + action +
                ", level=" + riskLevel + ", score=" + score + "}";
    }
}