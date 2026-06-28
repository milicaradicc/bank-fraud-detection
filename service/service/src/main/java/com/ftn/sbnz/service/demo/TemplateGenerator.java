package com.ftn.sbnz.service.demo;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;

import java.io.FileInputStream;
import java.io.InputStream;

public class TemplateGenerator {

    public static void main(String[] args) throws Exception {
        String basePath = "C:\\Users\\Lenovo\\Desktop\\projects\\bank-fraud-detection\\kjar\\kjar\\src\\main\\resources\\templatetable\\";

        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();

        // --- Template 1: Risk thresholds ---
        InputStream t1 = new FileInputStream(basePath + "risk_thresholds.drt");
        InputStream d1 = new FileInputStream(basePath + "risk_thresholds_data.xls");
        String drl1 = converter.compile(d1, t1, 3, 2);
        System.out.println("=== RISK THRESHOLDS DRL ===");
        System.out.println(drl1);

        // --- Template 2: MCC risk ---
        InputStream t2 = new FileInputStream(basePath + "mcc_risk.drt");
        InputStream d2 = new FileInputStream(basePath + "mcc_risk_data.xls");
        String drl2 = converter.compile(d2, t2, 3, 2);
        System.out.println("\n=== MCC RISK DRL ===");
        System.out.println(drl2);

        // --- Template 3: Country lists (crna/siva lista zemalja) ---
        InputStream t3 = new FileInputStream(basePath + "country_lists.drt");
        InputStream d3 = new FileInputStream(basePath + "country_lists_data.xls");
        String drl3 = converter.compile(d3, t3, 3, 2);
        System.out.println("\n\n=== COUNTRY LISTS DRL ===");
        System.out.println(drl3);
    }
}