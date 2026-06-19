package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.model.facts.Flag;
import com.ftn.sbnz.model.enums.FlagType;
import com.ftn.sbnz.service.dto.DiagnosticResult;
import com.ftn.sbnz.service.service.DiagnosticService;
import org.kie.api.runtime.KieSession;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;
    private final KieSession kieSession;

    public DiagnosticController(DiagnosticService diagnosticService, KieSession kieSession) {
        this.diagnosticService = diagnosticService;
        this.kieSession = kieSession;
    }

    @GetMapping("/account-takeover/{clientId}")
    public DiagnosticResult checkAccountTakeover(@PathVariable String clientId) {
        boolean confirmed = diagnosticService.checkAccountTakeover(clientId);

        DiagnosticResult result = new DiagnosticResult("ACCOUNT_TAKEOVER", clientId, confirmed);
        result.setEvidenceChain(getEvidenceChain(clientId,
                FlagType.BRUTE_FORCE_POKUSAJ, FlagType.SUSPICIOUS_LOGIN,
                FlagType.IZMENA_KONTAKT_PODATAKA, FlagType.NOVI_PRIMALAC,
                FlagType.ABNORMALNO_VISOKA));
        result.setExplanation(confirmed
                ? "Hipoteza POTVRĐENA: kompromitovan pristup (brute force ili suspicious login) " +
                "U KOMBINACIJI sa izmenom kontakta ili novim primaocem I abnormalno visokim iznosom."
                : "Hipoteza NIJE potvrđena — nedostaje barem jedan od potrebnih dokaza.");
        return result;
    }

    @GetMapping("/app-scam/{clientId}")
    public DiagnosticResult checkAppScam(@PathVariable String clientId) {
        boolean confirmed = diagnosticService.checkAppScam(clientId);

        DiagnosticResult result = new DiagnosticResult("APP_SCAM", clientId, confirmed);
        result.setEvidenceChain(getEvidenceChain(clientId,
                FlagType.PRVI_TRANSFER_KA_PRIMAOCU, FlagType.ABNORMALNO_VISOKA,
                FlagType.OBRAZAC_POD_STRESOM, FlagType.OSETLJIV_SEGMENT));
        result.setExplanation(confirmed
                ? "Hipoteza POTVRĐENA: prvi transfer ka primaocu I neuobičajen iznos I " +
                "(obrazac pod stresom ILI osetljiv segment)."
                : "Hipoteza NIJE potvrđena — nedostaje barem jedan od potrebnih dokaza.");
        return result;
    }

    @GetMapping("/money-mule")
    public DiagnosticResult checkMoneyMule(@RequestParam String from, @RequestParam String to) {
        boolean confirmed = diagnosticService.checkMoneyMule(from, to);

        DiagnosticResult result = new DiagnosticResult("MONEY_MULE", from, confirmed);
        result.setExplanation(confirmed
                ? "Lanac transfera POSTOJI: novac sa naloga " + from + " stiže do naloga " + to + " kroz proizvoljno mnogo posrednika."
                : "Lanac transfera NE POSTOJI između " + from + " i " + to + ".");
        return result;
    }

    @GetMapping("/money-mule/network/{clientId}")
    public List<String> getMuleNetwork(@PathVariable String clientId) {
        return diagnosticService.getMuleNetwork(clientId);
    }

    @GetMapping("/all/account-takeover")
    public List<String> getAllAccountTakeoverCases() {
        return diagnosticService.getAllAccountTakeoverCases();
    }

    @GetMapping("/all/app-scam")
    public List<String> getAllAppScamCases() {
        return diagnosticService.getAllAppScamCases();
    }

    private List<DiagnosticResult.EvidenceItem> getEvidenceChain(String clientId, FlagType... types) {
        Set<FlagType> typeSet = Set.of(types);
        return kieSession.getObjects(o -> o instanceof Flag)
                .stream().map(o -> (Flag) o)
                .filter(f -> f.getClientId().equals(clientId) && typeSet.contains(f.getType()))
                .sorted(Comparator.comparing(Flag::getTimestamp))
                .map(f -> new DiagnosticResult.EvidenceItem(
                        f.getType().name(), f.getDescription(), f.getTimestamp(), f.isCritical()))
                .collect(Collectors.toList());
    }
}