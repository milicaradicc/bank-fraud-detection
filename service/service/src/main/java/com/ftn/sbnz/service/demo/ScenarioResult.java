package com.ftn.sbnz.service.demo;

import com.ftn.sbnz.model.facts.Alert;
import com.ftn.sbnz.model.facts.Flag;
import com.ftn.sbnz.model.facts.RiskScore;

import java.util.ArrayList;
import java.util.List;

public class ScenarioResult {

    private String scenarioName;
    private String description;
    private List<StepLog> steps = new ArrayList<>();
    private List<Flag> flags = new ArrayList<>();
    private RiskScore finalScore;
    private Alert finalAlert;
    private String summary;

    public ScenarioResult() {}

    public ScenarioResult(String scenarioName, String description) {
        this.scenarioName = scenarioName;
        this.description = description;
    }

    public void addStep(String time, String action, String detail) {
        steps.add(new StepLog(time, action, detail));
    }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<StepLog> getSteps() { return steps; }
    public void setSteps(List<StepLog> steps) { this.steps = steps; }

    public List<Flag> getFlags() { return flags; }
    public void setFlags(List<Flag> flags) { this.flags = flags; }

    public RiskScore getFinalScore() { return finalScore; }
    public void setFinalScore(RiskScore finalScore) { this.finalScore = finalScore; }

    public Alert getFinalAlert() { return finalAlert; }
    public void setFinalAlert(Alert finalAlert) { this.finalAlert = finalAlert; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public static class StepLog {
        private String time;
        private String action;
        private String detail;

        public StepLog() {}

        public StepLog(String time, String action, String detail) {
            this.time = time;
            this.action = action;
            this.detail = detail;
        }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
    }
}