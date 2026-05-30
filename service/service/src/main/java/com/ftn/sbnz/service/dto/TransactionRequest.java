package com.ftn.sbnz.service.dto;

import com.ftn.sbnz.model.enums.TransactionChannel;

public class TransactionRequest {
    private String clientId;
    private double amount;
    private String currency;
    private TransactionChannel channel;
    private String country;
    private String city;
    private String deviceId;
    private String recipientId;
    private boolean recipientIsNew;
    private boolean recipientIsForeignAccount;
    private String recipientCountry;
    private boolean inflow;
    private String merchantCategory;
    private int mccCode;
    private double latitude;
    private double longitude;

    // getteri i setteri
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public TransactionChannel getChannel() { return channel; }
    public void setChannel(TransactionChannel channel) { this.channel = channel; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public boolean isRecipientIsNew() { return recipientIsNew; }
    public void setRecipientIsNew(boolean v) { this.recipientIsNew = v; }
    public boolean isRecipientIsForeignAccount() { return recipientIsForeignAccount; }
    public void setRecipientIsForeignAccount(boolean v) { this.recipientIsForeignAccount = v; }
    public String getRecipientCountry() { return recipientCountry; }
    public void setRecipientCountry(String v) { this.recipientCountry = v; }
    public boolean isInflow() { return inflow; }
    public void setInflow(boolean inflow) { this.inflow = inflow; }
    public String getMerchantCategory() { return merchantCategory; }
    public void setMerchantCategory(String v) { this.merchantCategory = v; }
    public int getMccCode() { return mccCode; }
    public void setMccCode(int mccCode) { this.mccCode = mccCode; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}