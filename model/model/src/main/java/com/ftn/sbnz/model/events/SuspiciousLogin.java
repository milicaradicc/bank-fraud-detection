package com.ftn.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("8d")
public class SuspiciousLogin {

    private String clientId;
    private Date timestamp;
    private String country;
    private String deviceId;
    private boolean newDevice;
    private boolean newCountry;
    private String reason;

    public SuspiciousLogin() {}

    public SuspiciousLogin(String clientId, Date timestamp, String reason) {
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public boolean isNewDevice() { return newDevice; }
    public void setNewDevice(boolean newDevice) { this.newDevice = newDevice; }

    public boolean isNewCountry() { return newCountry; }
    public void setNewCountry(boolean newCountry) { this.newCountry = newCountry; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "SuspiciousLogin{client=" + clientId + ", reason=" + reason + "}";
    }
}