package com.ftn.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("8d")
public class LoginEvent {

    private String eventId;
    private String clientId;
    private Date timestamp;
    private String ipAddress;
    private String country;
    private double latitude;
    private double longitude;
    private String deviceId;
    private boolean successful;
    private boolean knownDevice;

    public LoginEvent() {}

    public LoginEvent(String eventId, String clientId, Date timestamp,
                      boolean successful, String deviceId) {
        this.eventId = eventId;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.successful = successful;
        this.deviceId = deviceId;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }

    public boolean isKnownDevice() { return knownDevice; }
    public void setKnownDevice(boolean knownDevice) { this.knownDevice = knownDevice; }

    @Override
    public String toString() {
        return "LoginEvent{client=" + clientId + ", successful=" + successful +
                ", country=" + country + "}";
    }
}