package com.ftn.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("8d")
public class AccountChange {

    public enum ChangeType {
        PASSWORD_CHANGE,
        EMAIL_CHANGE,
        PHONE_CHANGE,
        LIMIT_INCREASE,
        NEW_RECIPIENT,
        CONTACT_CHANGE
    }

    private String eventId;
    private String clientId;
    private Date timestamp;
    private ChangeType changeType;
    private String oldValue;
    private String newValue;

    public AccountChange() {}

    public AccountChange(String eventId, String clientId, Date timestamp, ChangeType changeType) {
        this.eventId = eventId;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.changeType = changeType;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public ChangeType getChangeType() { return changeType; }
    public void setChangeType(ChangeType changeType) { this.changeType = changeType; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    /**
     * Da li je promena povezana sa kontakt podacima (email, phone).
     */
    public boolean isContactChange() {
        return changeType == ChangeType.EMAIL_CHANGE
                || changeType == ChangeType.PHONE_CHANGE
                || changeType == ChangeType.CONTACT_CHANGE;
    }

    @Override
    public String toString() {
        return "AccountChange{client=" + clientId + ", type=" + changeType + "}";
    }
}