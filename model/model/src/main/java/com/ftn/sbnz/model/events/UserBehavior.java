package com.ftn.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("2h")
public class UserBehavior {

    public enum BehaviorType {
        LIMIT_SEARCH,           // pretraga limita
        URGENT_TRANSFER_HELP,   // čitanje uputstva za hitne prenose
        FAILED_TRANSFER,        // neuspeo transfer
        FAQ_FRAUD_PAGE          // čitanje FAQ o prevarama
    }

    private String clientId;
    private Date timestamp;
    private BehaviorType behaviorType;

    public UserBehavior() {}

    public UserBehavior(String clientId, Date timestamp, BehaviorType behaviorType) {
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.behaviorType = behaviorType;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public BehaviorType getBehaviorType() { return behaviorType; }
    public void setBehaviorType(BehaviorType behaviorType) { this.behaviorType = behaviorType; }

    @Override
    public String toString() {
        return "UserBehavior{client=" + clientId + ", type=" + behaviorType + "}";
    }
}