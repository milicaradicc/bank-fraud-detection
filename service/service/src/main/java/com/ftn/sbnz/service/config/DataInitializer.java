package com.ftn.sbnz.service.config;

import com.ftn.sbnz.model.enums.*;
import com.ftn.sbnz.model.events.Transaction;
import com.ftn.sbnz.model.facts.Client;
import com.ftn.sbnz.model.facts.ConfigList;
import com.ftn.sbnz.model.facts.Flag;
import com.ftn.sbnz.service.repository.UserRepository;
import com.ftn.sbnz.service.service.AuthService;
import com.ftn.sbnz.service.service.TransactionService;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * Inicijalizacija baze znanja za demo odbrane.
 *
 * Klijenti su podeseni tako da svaki demonstrira odredjeni mehanizam:
 *
 *  C-001 Marija   (REGULAR)    - CISTA, za rucni unos transakcije na odbrani
 *  C-002 Petar    (REGULAR)    - CIST, kontrola (dijagnostika vraca NISTA)
 *  C-003 Ana      (REGULAR)    - MONEY MULE: lanac transfera C-003 -> M-1 -> M-2 -> M-3
 *  C-004 Jovan    (VIP)        - ACCOUNT TAKEOVER: seed flagovi za ATO hipotezu
 *  C-005 Milena   (PENSIONER)  - APP SCAM: seed flagovi za app scam hipotezu
 *  C-007 Ivana    (YOUNG)      - za segment-template demo (nizak prag)
 *
 * Napomena: C-004 i C-005 NEMAJU transakciju u seed-u namerno - njihovi flagovi
 * sluze SAMO za backward chaining dijagnostiku. Bez transakcije, L3 se ne okida
 * za njih, pa nema skorovanja ni watchlist petlje.
 *
 * CEP se demonstrira posebno preko /api/v1/demo/cep-all (pseudo clock).
 */
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
        // ── Konfiguracija ───────────────────────────────────────────
        ConfigList config = new ConfigList();
        config.setAmlReportingThreshold(15000.0);
        config.setBlackListedCountries(new HashSet<>(Set.of("KP", "IR", "SY", "CU")));
        config.setGrayListedCountries(new HashSet<>(Set.of("KY", "PA", "AE", "MT", "CY", "RU", "BY")));
        config.setHighRiskMccCodes(new HashSet<>(Set.of(7995, 6010, 6011, 4829, 6051, 6050)));
        kieSession.insert(config);

        // ── Klijenti ────────────────────────────────────────────────

        // C-001 Marija - CISTA (za rucni unos na odbrani)
        Client c1 = new Client("C-001", "Marija Nikolić", 45, ClientSegment.REGULAR,
                LocalDate.of(2019, 3, 15), "RS", 2500, 300);
        c1.setKnownDevices(new HashSet<>(Set.of("DEV-611B38E2", "DEV-002")));
        c1.setKnownRecipients(new HashSet<>(Set.of("R-001", "R-002", "R-003")));
        kieSession.insert(c1);

        // C-002 Petar - CIST (kontrola)
        Client c2 = new Client("C-002", "Petar Jović", 34, ClientSegment.REGULAR,
                LocalDate.of(2018, 7, 22), "RS", 1800, 200);
        c2.setKnownDevices(new HashSet<>(Set.of("DEV-611B38E2", "DEV-011", "DEV-012")));
        c2.setKnownRecipients(new HashSet<>(Set.of("R-010", "R-011")));
        kieSession.insert(c2);

        // C-003 Ana - MONEY MULE (lanac transfera, bez seed flagova)
        Client c3 = new Client("C-003", "Ana Stojanović", 29, ClientSegment.REGULAR,
                LocalDate.of(2020, 11, 8), "RS", 3200, 400);
        kieSession.insert(c3);

        // C-004 Jovan - ACCOUNT TAKEOVER (VIP) - bez transakcije
        Client c4 = new Client("C-004", "Jovan Đorđević", 52, ClientSegment.VIP,
                LocalDate.of(2012, 2, 14), "RS", 15000, 1500);
        kieSession.insert(c4);

        // C-005 Milena - APP SCAM (PENSIONER) - bez transakcije
        Client c5 = new Client("C-005", "Milena Pavlović", 68, ClientSegment.PENSIONER,
                LocalDate.of(2015, 6, 30), "RS", 800, 100);
        kieSession.insert(c5);

        // C-007 Ivana - YOUNG (segment template demo)
        Client c7 = new Client("C-007", "Ivana Marković", 22, ClientSegment.YOUNG,
                LocalDate.of(2024, 1, 10), "RS", 600, 80);
        kieSession.insert(c7);

        // ════════════════════════════════════════════════════════════
        //  MONEY MULE LANAC (C-003 -> M-1 -> M-2 -> M-3)
        //  reaches(C-003, M-3) = true; getMuleNetwork(C-003) = [M-1, M-2, M-3]
        //  Iznosi su mali (ispod 5x prosek 400 = 2000) da ne okidaju ABNORMALNO_VISOKA
        // ════════════════════════════════════════════════════════════
        Date now = new Date();

        Transaction mule1 = new Transaction("TX-MULE-1", "C-003", 1800, now, TransactionChannel.TRANSFER);
        mule1.setCurrency("EUR");
        mule1.setCountry("RS");
        mule1.setRecipientId("M-1");
        mule1.setDeviceId("DEV-611B38E2");
        mule1.setInflow(false);
        transactionService.addToLog(mule1);
        kieSession.insert(mule1);

        Transaction mule2 = new Transaction("TX-MULE-2", "M-1", 1700, now, TransactionChannel.TRANSFER);
        mule2.setCurrency("EUR");
        mule2.setCountry("RS");
        mule2.setRecipientId("M-2");
        mule2.setInflow(false);
        transactionService.addToLog(mule2);
        kieSession.insert(mule2);

        Transaction mule3 = new Transaction("TX-MULE-3", "M-2", 1600, now, TransactionChannel.TRANSFER);
        mule3.setCurrency("EUR");
        mule3.setCountry("RS");
        mule3.setRecipientId("M-3");
        mule3.setInflow(false);
        transactionService.addToLog(mule3);
        kieSession.insert(mule3);

        // ════════════════════════════════════════════════════════════
        //  ACCOUNT TAKEOVER flagovi za C-004 (Jovan)
        //  query accountTakeover: compromisedAccess (BRUTE_FORCE ili SUSPICIOUS_LOGIN)
        //    + (IZMENA_KONTAKT ili NOVI_PRIMALAC) + ABNORMALNO_VISOKA
        // ════════════════════════════════════════════════════════════
        kieSession.insert(new Flag("C-004", "TX-ATO-1", FlagType.BRUTE_FORCE_POKUSAJ, now,
                "5 neuspelih prijava u 3 min sa IP iz inostranstva"));
        kieSession.insert(new Flag("C-004", "TX-ATO-2", FlagType.IZMENA_KONTAKT_PODATAKA, now,
                "Promena email adrese za notifikacije"));
        kieSession.insert(new Flag("C-004", "TX-ATO-3", FlagType.NOVI_PRIMALAC, now,
                "Dodat novi primalac - racun u inostranstvu"));
        kieSession.insert(new Flag("C-004", "TX-ATO-4", FlagType.ABNORMALNO_VISOKA, now,
                "Transfer 4800 EUR - visestruko iznad proseka"));

        // ════════════════════════════════════════════════════════════
        //  APP SCAM flagovi za C-005 (Milena)
        //  query appScam: PRVI_TRANSFER_KA_PRIMAOCU + ABNORMALNO_VISOKA
        //    + (OBRAZAC_POD_STRESOM ili OSETLJIV_SEGMENT)
        // ════════════════════════════════════════════════════════════
        kieSession.insert(new Flag("C-005", "TX-APP-1", FlagType.PRVI_TRANSFER_KA_PRIMAOCU, now,
                "Prvi transfer ka ovom primaocu"));
        kieSession.insert(new Flag("C-005", "TX-APP-2", FlagType.ABNORMALNO_VISOKA, now,
                "Transfer 6000 EUR - 30x prosek klijenta"));
        kieSession.insert(new Flag("C-005", "TX-APP-3", FlagType.OSETLJIV_SEGMENT, now,
                "Klijent u osetljivom segmentu (penzioner)"));
        kieSession.insert(new Flag("C-005", "TX-APP-4", FlagType.OBRAZAC_POD_STRESOM, now,
                "Vise pretraga limita i citanje uputstva pre transfera"));

        // ── Normalna transakcija za C-002 (da dashboard nije prazan) ─
        Transaction t2 = new Transaction("TX-00420", "C-002", 320, now, TransactionChannel.POS);
        t2.setCurrency("EUR");
        t2.setCountry("RS");
        t2.setCity("Beograd");
        t2.setDeviceId("DEV-011");
        t2.setMerchantCategory("GROCERY");
        t2.setMccCode(5411);
        transactionService.addToLog(t2);
        kieSession.insert(t2);

        kieSession.fireAllRules();
    }
}