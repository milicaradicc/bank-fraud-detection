package com.ftn.sbnz.service.dto;

import java.util.Date;
import java.util.List;

public class DiagnosticResult {
    private String hypothesis;
    private String clientId;
    private boolean confirmed;
    private List<EvidenceItem> evidenceChain;
    private String explanation;

    public static class EvidenceItem {
        private String flagType;
        private String description;
        private Date timestamp;
        private boolean critical;

        public EvidenceItem() {}

        public EvidenceItem(String flagType, String description, Date timestamp, boolean critical) {
            this.flagType = flagType;
            this.description = description;
            this.timestamp = timestamp;
            this.critical = critical;
        }

        public String getFlagType() { return flagType; }
        public void setFlagType(String flagType) { this.flagType = flagType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        public boolean isCritical() { return critical; }
        public void setCritical(boolean critical) { this.critical = critical; }
    }

    public DiagnosticResult() {}

    public DiagnosticResult(String hypothesis, String clientId, boolean confirmed) {
        this.hypothesis = hypothesis;
        this.clientId = clientId;
        this.confirmed = confirmed;
    }

    public String getHypothesis() { return hypothesis; }
    public void setHypothesis(String hypothesis) { this.hypothesis = hypothesis; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    public List<EvidenceItem> getEvidenceChain() { return evidenceChain; }
    public void setEvidenceChain(List<EvidenceItem> evidenceChain) { this.evidenceChain = evidenceChain; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}