package com.ftn.sbnz.model.entities;

import com.ftn.sbnz.model.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // bcrypt hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Opciono: veza ka Client faktu (samo za CLIENT rolu)
    private String linkedClientId;

    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getLinkedClientId() { return linkedClientId; }
    public void setLinkedClientId(String linkedClientId) { this.linkedClientId = linkedClientId; }
}