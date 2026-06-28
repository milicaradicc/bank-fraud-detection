package com.ftn.sbnz.service.demo;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;

import java.io.FileInputStream;
import java.io.InputStream;

public class TemplateGenerator {

    public static void main(String[] args) throws Exception {
        String basePath = "C:\\Users\\Lenovo\\Desktop\\projects\\bank-fraud-detection\\kjar\\kjar\\src\\main\\rule-templates\\rules\\templates\\";

        InputStream template = new FileInputStream(basePath + "risk_thresholds.drt");
        InputStream data = new FileInputStream(basePath + "risk_thresholds_data.xls");

        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        String drl = converter.compile(data, template, 3, 2);

        System.out.println("=== GENERISANI DRL — kopiraj u kjar/src/main/resources/rules/templates/risk_thresholds_generated.drl ===");
        System.out.println(drl);
    }
}