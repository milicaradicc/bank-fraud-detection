package com.ftn.sbnz.service.repository;

import com.ftn.sbnz.model.entities.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {
    Optional<ClientUser> findByUsername(String username);
}