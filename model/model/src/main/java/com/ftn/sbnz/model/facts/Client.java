package com.ftn.sbnz.model.facts;

import com.ftn.sbnz.model.enums.ClientSegment;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Client {

    private String clientId;
    private String name;
    private int age;
    private ClientSegment segment;
    private LocalDate accountOpenedDate;
    private String countryOfResidence;
    private double averageMonthlyTurnover;
    private double averageTransactionAmount;

    private Set<String> knownDevices = new HashSet<>();
    private Set<String> knownRecipients = new HashSet<>();

    private boolean hasPreviousFraudCase;
    private boolean kycFailed;
    private int daysInactiveBeforeReactivation; // 0 ako nije bio neaktivan
    private double historicalOutflowInflowRatio; // istorijski odnos (npr. 0.3)

    private boolean onWatchlist;
    private int blockedTransactionsLast7Days;

    public Client() {}

    public Client(String clientId, String name, int age, ClientSegment segment,
                  LocalDate accountOpenedDate, String countryOfResidence,
                  double averageMonthlyTurnover, double averageTransactionAmount) {
        this.clientId = clientId;
        this.name = name;
        this.age = age;
        this.segment = segment;
        this.accountOpenedDate = accountOpenedDate;
        this.countryOfResidence = countryOfResidence;
        this.averageMonthlyTurnover = averageMonthlyTurnover;
        this.averageTransactionAmount = averageTransactionAmount;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public ClientSegment getSegment() { return segment; }
    public void setSegment(ClientSegment segment) { this.segment = segment; }

    public LocalDate getAccountOpenedDate() { return accountOpenedDate; }
    public void setAccountOpenedDate(LocalDate accountOpenedDate) { this.accountOpenedDate = accountOpenedDate; }

    public String getCountryOfResidence() { return countryOfResidence; }
    public void setCountryOfResidence(String countryOfResidence) { this.countryOfResidence = countryOfResidence; }

    public double getAverageMonthlyTurnover() { return averageMonthlyTurnover; }
    public void setAverageMonthlyTurnover(double averageMonthlyTurnover) { this.averageMonthlyTurnover = averageMonthlyTurnover; }

    public double getAverageTransactionAmount() { return averageTransactionAmount; }
    public void setAverageTransactionAmount(double averageTransactionAmount) { this.averageTransactionAmount = averageTransactionAmount; }

    public Set<String> getKnownDevices() { return knownDevices; }
    public void setKnownDevices(Set<String> knownDevices) { this.knownDevices = knownDevices; }

    public Set<String> getKnownRecipients() { return knownRecipients; }
    public void setKnownRecipients(Set<String> knownRecipients) { this.knownRecipients = knownRecipients; }

    public boolean isHasPreviousFraudCase() { return hasPreviousFraudCase; }
    public void setHasPreviousFraudCase(boolean hasPreviousFraudCase) { this.hasPreviousFraudCase = hasPreviousFraudCase; }

    public boolean isKycFailed() { return kycFailed; }
    public void setKycFailed(boolean kycFailed) { this.kycFailed = kycFailed; }

    public int getDaysInactiveBeforeReactivation() { return daysInactiveBeforeReactivation; }
    public void setDaysInactiveBeforeReactivation(int daysInactiveBeforeReactivation) { this.daysInactiveBeforeReactivation = daysInactiveBeforeReactivation; }

    public double getHistoricalOutflowInflowRatio() { return historicalOutflowInflowRatio; }
    public void setHistoricalOutflowInflowRatio(double historicalOutflowInflowRatio) { this.historicalOutflowInflowRatio = historicalOutflowInflowRatio; }

    public boolean isOnWatchlist() { return onWatchlist; }
    public void setOnWatchlist(boolean onWatchlist) { this.onWatchlist = onWatchlist; }

    public int getBlockedTransactionsLast7Days() { return blockedTransactionsLast7Days; }
    public void setBlockedTransactionsLast7Days(int blockedTransactionsLast7Days) { this.blockedTransactionsLast7Days = blockedTransactionsLast7Days; }

    public long daysSinceAccountOpened() {
        if (accountOpenedDate == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(accountOpenedDate, LocalDate.now());
    }

    public boolean isKnownDevice(String deviceId) {
        return knownDevices.contains(deviceId);
    }

    public boolean isKnownRecipient(String recipientId) {
        return knownRecipients.contains(recipientId);
    }

    @Override
    public String toString() {
        return "Client{id=" + clientId + ", name=" + name + ", segment=" + segment + "}";
    }
}