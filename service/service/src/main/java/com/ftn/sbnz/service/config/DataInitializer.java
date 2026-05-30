package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.enums.Role;
import com.ftn.sbnz.service.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;

    public DataInitializer(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        try { authService.register("admin", "admin123", Role.ADMIN); } catch (Exception ignored) {}
        try { authService.register("analyst1", "analyst123", Role.ANALYST); } catch (Exception ignored) {}
        try { authService.register("client1", "client123", Role.CLIENT); } catch (Exception ignored) {}
    }
}