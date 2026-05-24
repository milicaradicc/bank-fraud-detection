package com.ftn.sbnz.model.enums;

public enum ActionType {
    LOG_ONLY,           // Nizak rizik - samo loguj
    ALERT_ANALYST,      // Srednji rizik - alert za analitičara
    BLOCK_STEPUP,       // Visok rizik - blokiraj + step-up autentifikacija
    FREEZE_ACCOUNT,     // Kritičan rizik - zamrzni nalog
    AML_REPORT          // Prijava AML službi
}