package com.ftn.sbnz.model.events;

import com.ftn.sbnz.model.enums.TransactionChannel;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("49h")
public class Transaction {

    private String transactionId;
    private String clientId;
    private double amount;
    private String currency;
    private Date timestamp;

    private TransactionChannel channel;

    // Lokacija
    private String country;
    private String city;
    private String ipAddress;
    private double latitude;
    private double longitude;

    // Merchant (za POS/online)
    private String merchantId;
    private String merchantCategory; // MCC kod ili kategorija
    private int mccCode;

    // Uređaj
    private String deviceId;

    // Za transfere
    private String recipientId;
    private boolean recipientIsNew;
    private String recipientCountry;
    private boolean recipientIsForeignAccount;
    private boolean recipientIsNewlyOpenedAccount;

    // Smer toka
    private boolean inflow; // true = priliv, false = odliv

    public Transaction() {}

    public Transaction(String transactionId, String clientId, double amount,
                       Date timestamp, TransactionChannel channel) {
        this.transactionId = transactionId;
        this.clientId = clientId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.channel = channel;
    }

    // Getteri i setteri
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public TransactionChannel getChannel() { return channel; }
    public void setChannel(TransactionChannel channel) { this.channel = channel; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getMerchantCategory() { return merchantCategory; }
    public void setMerchantCategory(String merchantCategory) { this.merchantCategory = merchantCategory; }

    public int getMccCode() { return mccCode; }
    public void setMccCode(int mccCode) { this.mccCode = mccCode; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public boolean isRecipientIsNew() { return recipientIsNew; }
    public void setRecipientIsNew(boolean recipientIsNew) { this.recipientIsNew = recipientIsNew; }

    public String getRecipientCountry() { return recipientCountry; }
    public void setRecipientCountry(String recipientCountry) { this.recipientCountry = recipientCountry; }

    public boolean isRecipientIsForeignAccount() { return recipientIsForeignAccount; }
    public void setRecipientIsForeignAccount(boolean recipientIsForeignAccount) { this.recipientIsForeignAccount = recipientIsForeignAccount; }

    public boolean isRecipientIsNewlyOpenedAccount() { return recipientIsNewlyOpenedAccount; }
    public void setRecipientIsNewlyOpenedAccount(boolean recipientIsNewlyOpenedAccount) { this.recipientIsNewlyOpenedAccount = recipientIsNewlyOpenedAccount; }

    public boolean isInflow() { return inflow; }
    public void setInflow(boolean inflow) { this.inflow = inflow; }

    public int getHourOfDay() {
        if (timestamp == null) return -1;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(timestamp);
        return cal.get(java.util.Calendar.HOUR_OF_DAY);
    }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ", clientId=" + clientId +
                ", amount=" + amount + ", channel=" + channel + "}";
    }
}