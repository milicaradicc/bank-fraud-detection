package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.enums.Role;
import com.ftn.sbnz.service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String token = authService.login(body.get("username"), body.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    // Samo ADMIN može da registruje nove korisnike — zaštiti ovu rutu u produkciji
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        authService.register(body.get("username"), body.get("password"), role);
        return ResponseEntity.ok(Map.of("message", "Korisnik kreiran"));
    }
}