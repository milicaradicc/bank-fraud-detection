package com.ftn.sbnz.model.enums;

public enum FlagType {
    ABNORMALNO_VISOKA(20, true),
    NEUOBICAJENO_VREME(10, false),
    RIZICNA_LOKACIJA(15, true),
    RIZICAN_MERCHANT(15, true),
    NEPOZNAT_UREDJAJ(15, true),

    BRUTE_FORCE_POKUSAJ(20, true),
    SUSPICIOUS_LOGIN(25, true),
    NEMOGUCE_PUTOVANJE(30, true),
    IZMENA_KONTAKT_PODATAKA(15, false),
    NOVI_PRIMALAC(15, false),

    SUMNJIVA_AKTIVACIJA(20, true),
    NEOCEKIVANI_PRILIV(15, false),
    MULING_OBRAZAC(25, true),
    PASS_THROUGH_AKTIVNOST(25, true),
    SUMNJIVI_PRIMAOC(15, false),

    PRVI_TRANSFER_KA_PRIMAOCU(10, false),
    NEUOBICAJEN_IZNOS(15, false),
    OBRAZAC_POD_STRESOM(20, true),
    RIZICAN_PRIMALAC(15, true),

    VELOCITY_ATTACK(25, true),
    STRUCTURING(30, true),
    BURST_NAKON_MIROVANJA(20, true),
    BURST_AKTIVNOSTI(20, true),

    NOVI_RACUN_VISOK_OBRT(15, false),
    OSETLJIV_SEGMENT(5, false),
    SIVA_LISTA_DRZAVA(10, false),
    PRETHODNA_PREVARA(15, false),
    KYC_NEUSPEH(15, false);

    private final int weight;
    private final boolean critical;

    FlagType(int weight, boolean critical) {
        this.weight = weight;
        this.critical = critical;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isCritical() {
        return critical;
    }
}