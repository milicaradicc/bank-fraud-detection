package com.ftn.sbnz.service.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100) // pokreni posle DataInitializer-a
public class CepDemoRunner implements CommandLineRunner {

    private final DemoScenariosService scenarios;

    public CepDemoRunner(DemoScenariosService scenarios) {
        this.scenarios = scenarios;
    }

    @Override
    public void run(String... args) {
        boolean runCep = false;
        for (String arg : args) {
            if ("cep-demo".equalsIgnoreCase(arg)) {
                runCep = true;
                break;
            }
        }
        if (!runCep) return;

        System.out.println();
        System.out.println("================================================================");
        System.out.println("  CEP DEMONSTRACIJA - pseudo sat (SessionPseudoClock)");
        System.out.println("  Simulacija svih 7 CEP pravila kroz vremenske prozore");
        System.out.println("================================================================");
        System.out.println();

        ScenarioResult result = scenarios.runAllCepScenario();

        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.println("KORACI SCENARIJA (pseudo vreme):");
        System.out.println("----------------------------------------------------------------");
        for (ScenarioResult.StepLog step : result.getSteps()) {
            System.out.println("  [" + step.getTime() + "] " + step.getAction()
                    + " -- " + step.getDetail());
        }

        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.println("OKINUTI FLAGOVI (rezultat CEP detekcije):");
        System.out.println("----------------------------------------------------------------");
        if (result.getFlags() != null && !result.getFlags().isEmpty()) {
            for (com.ftn.sbnz.model.facts.Flag f : result.getFlags()) {
                System.out.println("  [OK] " + f.getType() + " (tezina " + f.getWeight()
                        + ") -- " + f.getDescription());
            }
        } else {
            System.out.println("  (nema flagova)");
        }

        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.println("ZAKLJUCAK: " + result.getSummary());
        System.out.println("----------------------------------------------------------------");
        System.out.println();
        System.out.println(">> Sada otvori front -> Upozorenja da vidis CEP_ALL alert(e).");
        System.out.println();
    }
}