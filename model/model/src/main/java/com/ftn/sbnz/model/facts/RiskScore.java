package com.ftn.sbnz.model.facts;

import com.ftn.sbnz.model.enums.RiskLevel;
import org.kie.api.definition.type.ClassReactive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ClassReactive
public class RiskScore {

    private String clientId;
    private String transactionId;
    private double score;
    private RiskLevel level;
    private Date timestamp;

    private List<String> contributingFlags = new ArrayList<>();
    private double appliedMultiplier = 1.0;
    private String multiplierReason;

    public RiskScore() {}

    public RiskScore(String clientId, double score, Date timestamp) {
        this.clientId = clientId;
        this.score = score;
        this.timestamp = timestamp;
        this.level = computeLevel(score);
    }

    private RiskLevel computeLevel(double score) {
        if (score < 30) return RiskLevel.LOW;
        if (score < 60) return RiskLevel.MEDIUM;
        if (score < 85) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public double getScore() { return score; }
    public void setScore(double score) {
        this.score = Math.min(100.0, score);
        this.level = computeLevel(this.score);
    }

    public RiskLevel getLevel() { return level; }
    public void setLevel(RiskLevel level) { this.level = level; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public List<String> getContributingFlags() { return contributingFlags; }
    public void setContributingFlags(List<String> contributingFlags) { this.contributingFlags = contributingFlags; }

    public double getAppliedMultiplier() { return appliedMultiplier; }
    public void setAppliedMultiplier(double appliedMultiplier) { this.appliedMultiplier = appliedMultiplier; }

    public String getMultiplierReason() { return multiplierReason; }
    public void setMultiplierReason(String multiplierReason) { this.multiplierReason = multiplierReason; }

    @Override
    public String toString() {
        return "RiskScore{client=" + clientId + ", score=" + score +
                ", level=" + level + ", multiplier=" + appliedMultiplier + "}";
    }
}