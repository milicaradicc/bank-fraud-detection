package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.enums.*;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.model.facts.Client;
import com.ftn.sbnz.model.facts.ConfigList;
import com.ftn.sbnz.service.repository.UserRepository;
import com.ftn.sbnz.service.service.AuthService;
import com.ftn.sbnz.service.service.TransactionService;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final KieSession kieSession;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public DataInitializer(AuthService authService,
                           KieSession kieSession,
                           UserRepository userRepository,
                           TransactionService transactionService) {
        this.authService = authService;
        this.kieSession = kieSession;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        initWorkingMemory();
    }

    private void seedUsers() {
        try { authService.register("admin", "admin123", Role.ADMIN); } catch (Exception ignored) {}
        try { authService.register("analyst1", "analyst123", Role.ANALYST); } catch (Exception ignored) {}

        Map<String, String> clientLinks = Map.of(
                "marija.nikolic", "C-001",
                "petar.jovic", "C-002",
                "ana.stojanovic", "C-003",
                "jovan.djordjevic", "C-004",
                "milena.pavlovic", "C-005",
                "ivana.markovic", "C-007"
        );

        clientLinks.forEach((username, clientId) -> {
            try {
                authService.register(username, "client123", Role.CLIENT);
                userRepository.findByUsername(username).ifPresent(u -> {
                    u.setLinkedClientId(clientId);
                    userRepository.save(u);
                });
            } catch (Exception ignored) {}
        });

        userRepository.findByUsername("marija.nikolic").ifPresent(u -> {
            u.setEmail("milica.t.radic@gmail.com"); 
            userRepository.save(u);
        });
    }

    private void initWorkingMemory() {
        ConfigList config = new ConfigList();
        config.setAmlReportingThreshold(15000.0);
        config.setBlackListedCountries(new HashSet<>(Set.of("KP", "IR", "SY", "CU")));
        config.setGrayListedCountries(new HashSet<>(Set.of("KY", "PA", "AE", "MT", "CY", "RU", "BY")));
        config.setHighRiskMccCodes(new HashSet<>(Set.of(7995, 6010, 6011, 4829, 6051, 6050)));
        kieSession.insert(config);

        // ── Klijenti ────────────────────────────────────────────────

        Client c1 = new Client("C-001", "Marija Nikolić", 45, ClientSegment.REGULAR,
                LocalDate.of(2019, 3, 15), "RS", 2500, 300);
        c1.setOnWatchlist(true);
        c1.setKnownDevices(new HashSet<>(Set.of("DEV-611B38E2", "DEV-002")));
        c1.setKnownRecipients(new HashSet<>(Set.of("R-001", "R-002", "R-003")));
        kieSession.insert(c1);

        Client c2 = new Client("C-002", "Petar Jović", 34, ClientSegment.REGULAR,
                LocalDate.of(2018, 7, 22), "RS", 1800, 200);
        c2.setKnownDevices(new HashSet<>(Set.of("DEV-611B38E2", "DEV-011", "DEV-012")));
        kieSession.insert(c2);

        Client c3 = new Client("C-003", "Ana Stojanović", 29, ClientSegment.REGULAR,
                LocalDate.of(2020, 11, 8), "RS", 3200, 400);
        c3.setDaysInactiveBeforeReactivation(94);
        c3.setOnWatchlist(true);
        c3.setHistoricalOutflowInflowRatio(0.2);
        c3.setKnownDevices(new HashSet<>(Set.of("DEV-611B38E2")));
        kieSession.insert(c3);

        Client c4 = new Client("C-004", "Jovan Đorđević", 52, ClientSegment.VIP,
                LocalDate.of(2012, 2, 14), "RS", 15000, 1500);
        c4.setHistoricalOutflowInflowRatio(0.4);
        kieSession.insert(c4);

        Client c5 = new Client("C-005", "Milena Pavlović", 68, ClientSegment.PENSIONER,
                LocalDate.of(2015, 6, 30), "RS", 800, 100);
        c5.setOnWatchlist(true);
        kieSession.insert(c5);

        Client c7 = new Client("C-007", "Ivana Marković", 22, ClientSegment.YOUNG,
                LocalDate.of(2024, 1, 10), "RS", 600, 80);
        c7.setOnWatchlist(true);
        c7.setHistoricalOutflowInflowRatio(0.21);
        kieSession.insert(c7);

        // ── Transakcije ─────────────────────────────────────────────

        Transaction t1 = new Transaction("TX-00421", "C-001", 4800, new Date(), TransactionChannel.TRANSFER);
        t1.setCurrency("EUR");
        t1.setCountry("NL");
        t1.setCity("Amsterdam");
        t1.setDeviceId("DEV-999");
        t1.setRecipientId("R-NEW-001");
        t1.setRecipientIsNew(true);
        t1.setRecipientIsForeignAccount(true);
        t1.setRecipientCountry("NL");
        t1.setInflow(false);
        transactionService.addToLog(t1);
        kieSession.insert(t1);

        Transaction t2 = new Transaction("TX-00420", "C-002", 320, new Date(), TransactionChannel.POS);
        t2.setCurrency("EUR");
        t2.setCountry("RS");
        t2.setCity("Beograd");
        t2.setDeviceId("DEV-010");
        t2.setMerchantCategory("GROCERY");
        t2.setMccCode(5411);
        transactionService.addToLog(t2);
        kieSession.insert(t2);

        Transaction t3 = new Transaction("TX-00419", "C-003", 9200, new Date(), TransactionChannel.TRANSFER);
        t3.setCurrency("EUR");
        t3.setCountry("KY");
        t3.setCity("George Town");
        t3.setRecipientId("R-KY-001");
        t3.setRecipientIsNew(true);
        t3.setRecipientIsForeignAccount(true);
        t3.setRecipientCountry("KY");
        t3.setInflow(false);
        transactionService.addToLog(t3);
        kieSession.insert(t3);

        Transaction t4 = new Transaction("TX-00418", "C-004", 150, new Date(), TransactionChannel.ATM);
        t4.setCurrency("EUR");
        t4.setCountry("RS");
        t4.setCity("Novi Sad");
        transactionService.addToLog(t4);
        kieSession.insert(t4);

        Transaction t5 = new Transaction("TX-00417", "C-005", 7500, new Date(), TransactionChannel.TRANSFER);
        t5.setCurrency("EUR");
        t5.setCountry("RS");
        t5.setRecipientId("R-NEW-002");
        t5.setRecipientIsNew(true);
        t5.setInflow(false);
        transactionService.addToLog(t5);
        kieSession.insert(t5);

        Transaction t6 = new Transaction("TX-00415", "C-007", 14800, new Date(), TransactionChannel.TRANSFER);
        t6.setCurrency("EUR");
        t6.setCountry("RS");
        t6.setRecipientId("R-MUL-001");
        t6.setInflow(false);
        transactionService.addToLog(t6);
        kieSession.insert(t6);

        kieSession.fireAllRules();
    }
}