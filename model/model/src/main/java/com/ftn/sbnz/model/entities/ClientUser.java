package com.ftn.sbnz.model.entities;

import com.ftn.sbnz.model.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "client_users")
public class ClientUser extends User {

    private String clientId;

    public ClientUser() {
        super();
        setRole(Role.CLIENT);
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
}