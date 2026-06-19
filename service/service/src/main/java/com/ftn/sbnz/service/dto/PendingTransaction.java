package com.ftn.sbnz.service.dto;

public class PendingTransaction {
    private String transactionId;
    private String code;
    private long expiresAt;
    private int attempts;

    public PendingTransaction(String transactionId, String code, long expiresAt) {
        this.transactionId = transactionId;
        this.code = code;
        this.expiresAt = expiresAt;
        this.attempts = 0;
    }

    public String getTransactionId() { return transactionId; }
    public String getCode() { return code; }
    public long getExpiresAt() { return expiresAt; }
    public int getAttempts() { return attempts; }
    public void incrementAttempts() { this.attempts++; }
}