package com.ftn.sbnz.service.service;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DiagnosticService {

    private final KieSession kieSession;

    public DiagnosticService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public boolean checkAccountTakeover(String clientId) {
        QueryResults results = kieSession.getQueryResults("accountTakeover", clientId);
        return results.size() > 0;
    }

    public boolean checkAppScam(String clientId) {
        QueryResults results = kieSession.getQueryResults("appScam", clientId);
        return results.size() > 0;
    }

    public boolean checkMoneyMule(String fromAccount, String toAccount) {
        QueryResults results = kieSession.getQueryResults("reaches", fromAccount, toAccount);
        return results.size() > 0;
    }

    public List<String> getAllAccountTakeoverCases() {
        QueryResults results = kieSession.getQueryResults("accountTakeover", Variable.v);
        Set<String> accounts = new HashSet<>();
        for (QueryResultsRow row : results) {
            accounts.add((String) row.get("acc"));
        }
        return new ArrayList<>(accounts);
    }

    public List<String> getAllAppScamCases() {
        QueryResults results = kieSession.getQueryResults("appScam", Variable.v);
        Set<String> accounts = new HashSet<>();
        for (QueryResultsRow row : results) {
            accounts.add((String) row.get("acc"));
        }
        return new ArrayList<>(accounts);
    }

    public List<String> getMuleNetwork(String fromAccount) {
        QueryResults results = kieSession.getQueryResults("reaches", fromAccount, Variable.v);
        Set<String> reached = new HashSet<>();
        for (QueryResultsRow row : results) {
            reached.add((String) row.get("to"));
        }
        return new ArrayList<>(reached);
    }
}