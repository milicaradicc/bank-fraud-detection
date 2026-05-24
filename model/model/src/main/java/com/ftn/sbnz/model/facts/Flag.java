package com.ftn.sbnz.model.facts;

import com.ftn.sbnz.model.enums.FlagType;

import java.util.Date;
import java.util.Objects;

public class Flag {

    private String clientId;
    private String transactionId; // null ako je flag vezan samo za klijenta (npr. CEP burst)
    private FlagType type;
    private int weight;
    private Date timestamp;
    private String description;

    public Flag() {}

    public Flag(String clientId, FlagType type, Date timestamp) {
        this.clientId = clientId;
        this.type = type;
        this.weight = type.getWeight();
        this.timestamp = timestamp;
    }

    public Flag(String clientId, String transactionId, FlagType type, Date timestamp) {
        this(clientId, type, timestamp);
        this.transactionId = transactionId;
    }

    public Flag(String clientId, String transactionId, FlagType type, Date timestamp, String description) {
        this(clientId, transactionId, type, timestamp);
        this.description = description;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public FlagType getType() { return type; }
    public void setType(FlagType type) {
        this.type = type;
        this.weight = type.getWeight();
    }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCritical() {
        return type != null && type.isCritical();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flag)) return false;
        Flag flag = (Flag) o;
        return Objects.equals(clientId, flag.clientId)
                && Objects.equals(transactionId, flag.transactionId)
                && type == flag.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, transactionId, type);
    }

    @Override
    public String toString() {
        return "Flag{client=" + clientId + ", type=" + type + ", weight=" + weight + "}";
    }
}