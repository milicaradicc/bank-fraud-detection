package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.service.service.TemplateConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/templates")
public class TemplateConfigController {

    private final TemplateConfigService configService;

    public TemplateConfigController(TemplateConfigService configService) {
        this.configService = configService;
    }

    private static final String[] RISK_COLS = {"segment", "alertCut", "blockCut", "freezeCut"};
    private static final String[] RISK_TYPES = {"text", "num", "num", "num"};

    private static final String[] MCC_COLS = {"mcc", "category", "weight"};
    private static final String[] MCC_TYPES = {"num", "text", "num"};

    private static final String[] COUNTRY_COLS = {"countryCode", "listType", "weight"};
    private static final String[] COUNTRY_TYPES = {"text", "text", "num"};

    @GetMapping("/risk-thresholds")
    public ResponseEntity<List<Map<String, Object>>> getRisk() throws Exception {
        return ResponseEntity.ok(configService.readTable("risk_thresholds_data.xls", RISK_COLS));
    }

    @PostMapping("/risk-thresholds")
    public ResponseEntity<String> saveRisk(@RequestBody List<Map<String, Object>> rows) throws Exception {
        configService.writeTable("risk_thresholds_data.xls", RISK_COLS, RISK_TYPES, rows);
        return ResponseEntity.ok("Sačuvano. Pokrenite TemplateGenerator i restartujte backend.");
    }

    @GetMapping("/mcc-risk")
    public ResponseEntity<List<Map<String, Object>>> getMcc() throws Exception {
        return ResponseEntity.ok(configService.readTable("mcc_risk_data.xls", MCC_COLS));
    }

    @PostMapping("/mcc-risk")
    public ResponseEntity<String> saveMcc(@RequestBody List<Map<String, Object>> rows) throws Exception {
        configService.writeTable("mcc_risk_data.xls", MCC_COLS, MCC_TYPES, rows);
        return ResponseEntity.ok("Sačuvano. Pokrenite TemplateGenerator i restartujte backend.");
    }

    @GetMapping("/country-lists")
    public ResponseEntity<List<Map<String, Object>>> getCountry() throws Exception {
        return ResponseEntity.ok(configService.readTable("country_lists_data.xls", COUNTRY_COLS));
    }

    @PostMapping("/country-lists")
    public ResponseEntity<String> saveCountry(@RequestBody List<Map<String, Object>> rows) throws Exception {
        configService.writeTable("country_lists_data.xls", COUNTRY_COLS, COUNTRY_TYPES, rows);
        return ResponseEntity.ok("Sačuvano. Pokrenite TemplateGenerator i restartujte backend.");
    }
}