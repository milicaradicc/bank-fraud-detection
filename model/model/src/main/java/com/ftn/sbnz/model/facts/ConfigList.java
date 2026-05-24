package com.ftn.sbnz.model.facts;

import java.util.HashSet;
import java.util.Set;

public class ConfigList {

    private Set<String> blackListedCountries = new HashSet<>();
    private Set<String> grayListedCountries = new HashSet<>();
    private Set<String> blackListedIPs = new HashSet<>();
    private Set<String> blackListedMerchants = new HashSet<>();
    private Set<Integer> highRiskMccCodes = new HashSet<>();
    private Set<String> knownMuleAccounts = new HashSet<>();

    private double amlReportingThreshold = 15000.0;

    public ConfigList() {
        highRiskMccCodes.add(7995); // Gambling
        highRiskMccCodes.add(6051); // Crypto / non-financial institutions
        highRiskMccCodes.add(6050); // Quasi cash
    }

    public Set<String> getBlackListedCountries() { return blackListedCountries; }
    public void setBlackListedCountries(Set<String> blackListedCountries) { this.blackListedCountries = blackListedCountries; }

    public Set<String> getGrayListedCountries() { return grayListedCountries; }
    public void setGrayListedCountries(Set<String> grayListedCountries) { this.grayListedCountries = grayListedCountries; }

    public Set<String> getBlackListedIPs() { return blackListedIPs; }
    public void setBlackListedIPs(Set<String> blackListedIPs) { this.blackListedIPs = blackListedIPs; }

    public Set<String> getBlackListedMerchants() { return blackListedMerchants; }
    public void setBlackListedMerchants(Set<String> blackListedMerchants) { this.blackListedMerchants = blackListedMerchants; }

    public Set<Integer> getHighRiskMccCodes() { return highRiskMccCodes; }
    public void setHighRiskMccCodes(Set<Integer> highRiskMccCodes) { this.highRiskMccCodes = highRiskMccCodes; }

    public Set<String> getKnownMuleAccounts() { return knownMuleAccounts; }
    public void setKnownMuleAccounts(Set<String> knownMuleAccounts) { this.knownMuleAccounts = knownMuleAccounts; }

    public double getAmlReportingThreshold() { return amlReportingThreshold; }
    public void setAmlReportingThreshold(double amlReportingThreshold) { this.amlReportingThreshold = amlReportingThreshold; }

    public boolean isCountryGrayListed(String country) {
        return grayListedCountries.contains(country);
    }

    public boolean isCountryBlackListed(String country) {
        return blackListedCountries.contains(country);
    }

    public boolean isMccHighRisk(int mcc) {
        return highRiskMccCodes.contains(mcc);
    }
}