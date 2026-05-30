package com.ftn.sbnz.service.service;

import com.ftn.sbnz.model.entities.User;
import com.ftn.sbnz.model.enums.Role;
import com.ftn.sbnz.service.repository.UserRepository;
import com.ftn.sbnz.service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Pogrešna lozinka");
        }

        return jwtService.generateToken(user.getUsername(), user.getRole().name());
    }

    public void register(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Korisnik već postoji");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), role);
        userRepository.save(user);
    }
}