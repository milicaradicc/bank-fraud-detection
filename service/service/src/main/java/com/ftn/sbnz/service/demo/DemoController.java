package com.ftn.sbnz.service.demo;

import com.ftn.sbnz.service.demo.ScenarioResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST endpoint-i za demo scenarije.
 *
 * Sadrzi 10 demo endpoint-a koji zajedno aktiviraju sva pravila u sistemu.
 *
 * Endpoint-i:
 *   GET /api/v1/demo                     - lista svih dostupnih scenarija
 *   GET /api/v1/demo/marija              - Account Takeover (CEP + L1->L4)
 *   GET /api/v1/demo/mule                - Money Mule (accumulate + AML)
 *   GET /api/v1/demo/app-scam            - APP scam (multiplikator x1.3)
 *   GET /api/v1/demo/card-testing        - Velocity attack + VIP umanjenje
 *   GET /api/v1/demo/profile             - sva pravila profila
 *   GET /api/v1/demo/gambling            - neuobicajeno vreme + MCC
 *   GET /api/v1/demo/impossible-travel   - CEP geografska detekcija
 *   GET /api/v1/demo/structuring         - smurfing + AML
 *   GET /api/v1/demo/happy-path          - normalna transakcija (0 flagova)
 *   GET /api/v1/demo/cumulative          - watchlist boost + kumulativna
 *   GET /api/v1/demo/all                 - svih 10 scenarija odjednom
 */
@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    private final DemoScenariosService scenarios;

    @Autowired
    public DemoController(DemoScenariosService scenarios) {
        this.scenarios = scenarios;
    }

    @GetMapping
    public Map<String, Object> listScenarios() {
        return Map.of(
                "scenariji", List.of(
                        Map.of("br", 1, "path", "/api/v1/demo/marija",
                                "naziv", "Marija - Account Takeover",
                                "demonstrira", "CEP failed burst, suspicious login, ulancavanje L1->L2->L3->L4, multiplikator 3+ kriticnih"),
                        Map.of("br", 2, "path", "/api/v1/demo/mule",
                                "naziv", "Money Mule",
                                "demonstrira", "accumulate (collectSet, sum), muling pattern, pass-through, AML prijava"),
                        Map.of("br", 3, "path", "/api/v1/demo/app-scam",
                                "naziv", "APP Scam",
                                "demonstrira", "obrazac pod stresom, osetljiv segment multiplikator x1.3, BLOCK_STEPUP"),
                        Map.of("br", 4, "path", "/api/v1/demo/card-testing",
                                "naziv", "Card Testing - Velocity",
                                "demonstrira", "CEP velocity attack, VIP umanjenje -20%"),
                        Map.of("br", 5, "path", "/api/v1/demo/profile",
                                "naziv", "Suspicious Profile",
                                "demonstrira", "sva 5 pravila profila klijenta"),
                        Map.of("br", 6, "path", "/api/v1/demo/gambling",
                                "naziv", "Night Gambling",
                                "demonstrira", "L1 neuobicajeno vreme (02-05h), rizican merchant MCC 7995"),
                        Map.of("br", 7, "path", "/api/v1/demo/impossible-travel",
                                "naziv", "Impossible Travel",
                                "demonstrira", "CEP geografska detekcija sa GeoUtil haversine formulom"),
                        Map.of("br", 8, "path", "/api/v1/demo/structuring",
                                "naziv", "Structuring (Smurfing)",
                                "demonstrira", "CEP structuring + automatska AML prijava"),
                        Map.of("br", 9, "path", "/api/v1/demo/happy-path",
                                "naziv", "Happy Path",
                                "demonstrira", "sistem ne pravi lazne alarme - 0 flagova za normalnu transakciju"),
                        Map.of("br", 10, "path", "/api/v1/demo/cumulative",
                                "naziv", "Cumulative Escalation",
                                "demonstrira", "modify() feedback, watchlist boost MEDIUM->HIGH, kumulativna eskalacija")
                ),
                "kombinovani", Map.of(
                        "path", "/api/v1/demo/all",
                        "opis", "Svih 10 scenarija u jednom JSON-u")
        );
    }

    @GetMapping("/marija")
    public ResponseEntity<ScenarioResult> marija() {
        log.info("Scenario: Marija - Account Takeover");
        return ResponseEntity.ok(scenarios.runMarijaScenario());
    }

    @GetMapping("/mule")
    public ResponseEntity<ScenarioResult> mule() {
        log.info("Scenario: Money Mule");
        return ResponseEntity.ok(scenarios.runMoneyMuleScenario());
    }

    @GetMapping("/app-scam")
    public ResponseEntity<ScenarioResult> appScam() {
        log.info("Scenario: APP Scam");
        return ResponseEntity.ok(scenarios.runAppScamScenario());
    }

    @GetMapping("/card-testing")
    public ResponseEntity<ScenarioResult> cardTesting() {
        log.info("Scenario: Card Testing");
        return ResponseEntity.ok(scenarios.runCardTestingScenario());
    }

    @GetMapping("/profile")
    public ResponseEntity<ScenarioResult> profile() {
        log.info("Scenario: Suspicious Profile");
        return ResponseEntity.ok(scenarios.runSuspiciousProfileScenario());
    }

    @GetMapping("/gambling")
    public ResponseEntity<ScenarioResult> gambling() {
        log.info("Scenario: Night Gambling");
        return ResponseEntity.ok(scenarios.runNightGamblingScenario());
    }

    @GetMapping("/impossible-travel")
    public ResponseEntity<ScenarioResult> impossibleTravel() {
        log.info("Scenario: Impossible Travel");
        return ResponseEntity.ok(scenarios.runImpossibleTravelScenario());
    }

    @GetMapping("/structuring")
    public ResponseEntity<ScenarioResult> structuring() {
        log.info("Scenario: Structuring");
        return ResponseEntity.ok(scenarios.runStructuringScenario());
    }

    @GetMapping("/happy-path")
    public ResponseEntity<ScenarioResult> happyPath() {
        log.info("Scenario: Happy Path");
        return ResponseEntity.ok(scenarios.runHappyPathScenario());
    }

    @GetMapping("/cumulative")
    public ResponseEntity<ScenarioResult> cumulative() {
        log.info("Scenario: Cumulative Escalation");
        return ResponseEntity.ok(scenarios.runCumulativeEscalationScenario());
    }

    @GetMapping("/all")
    public List<ScenarioResult> all() {
        log.info("Pokrenuti svi scenariji");
        List<ScenarioResult> results = new ArrayList<>();
        results.add(scenarios.runMarijaScenario());
        results.add(scenarios.runMoneyMuleScenario());
        results.add(scenarios.runAppScamScenario());
        results.add(scenarios.runCardTestingScenario());
        results.add(scenarios.runSuspiciousProfileScenario());
        results.add(scenarios.runNightGamblingScenario());
        results.add(scenarios.runImpossibleTravelScenario());
        results.add(scenarios.runStructuringScenario());
        results.add(scenarios.runHappyPathScenario());
        results.add(scenarios.runCumulativeEscalationScenario());
        return results;
    }
}