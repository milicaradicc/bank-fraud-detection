import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type RiskLevel = 'low' | 'medium' | 'high' | 'critical';
export type FraudType = 'card_fraud' | 'account_takeover' | 'money_mule' | 'app_scam';

export interface Transaction {
  id: string;
  clientId: string;
  clientName: string;
  amount: number;
  currency: string;
  channel: 'POS' | 'ONLINE' | 'ATM' | 'TRANSFER';
  country: string;
  merchantCategory?: string;
  time: string;
  riskScore: number;
  riskLevel: RiskLevel;
  flags: string[];
  status: 'passed' | 'blocked' | 'pending' | 'frozen';
  fraudType?: FraudType;
}

export interface Alert {
  id: string;
  clientId: string;
  clientName: string;
  type: FraudType;
  riskScore: number;
  riskLevel: RiskLevel;
  time: string;
  flags: string[];
  status: 'new' | 'reviewing' | 'confirmed' | 'dismissed';
  description: string;
}

export interface Client {
  id: string;
  name: string;
  segment: 'VIP' | 'redovni' | 'mladi' | 'penzioner';
  age: number;
  country: string;
  avgMonthlyVolume: number;
  riskScore: number;
  riskLevel: RiskLevel;
  accountOpenDate: string;
  knownDevices: number;
  knownRecipients: number;
  onWatchlist: boolean;
  flags: string[];
}

export interface DiagnosticResult {
  hypothesis: string;
  clientId: string;
  confirmed: boolean;
  confidence: number;
  evidenceChain: { label: string; type: string; confirmed: boolean; detail: string }[];
  timestamp: string;
}

export interface Stats {
  totalTransactions: number;
  blockedToday: number;
  activeAlerts: number;
  criticalAlerts: number;
  fraudByType: { type: string; count: number; color: string }[];
  riskDistribution: { level: string; count: number; color: string }[];
  hourlyVolume: { hour: string; normal: number; suspicious: number }[];
}

@Injectable({ providedIn: 'root' })
export class FraudDataService {
  private transactions: Transaction[] = [
    { id: 'TX-00421', clientId: 'C-001', clientName: 'Marija Nikolić', amount: 4800, currency: 'EUR', channel: 'TRANSFER', country: 'NL', time: '22:27', riskScore: 100, riskLevel: 'critical', flags: ['NEPOZNAT_UREDJAJ','ABNORMALNO_VISOKA','NOVI_PRIMALAC','SUMNJIV_LOGIN'], status: 'blocked', fraudType: 'account_takeover' },
    { id: 'TX-00420', clientId: 'C-002', clientName: 'Petar Jović', amount: 320, currency: 'EUR', channel: 'POS', country: 'RS', time: '21:55', riskScore: 18, riskLevel: 'low', flags: [], status: 'passed' },
    { id: 'TX-00419', clientId: 'C-003', clientName: 'Ana Stojanović', amount: 9200, currency: 'EUR', channel: 'TRANSFER', country: 'KY', time: '21:40', riskScore: 72, riskLevel: 'high', flags: ['RIZICNA_LOKACIJA','RIZICAN_MERCHANT','NEUOBICAJENO_VREME'], status: 'pending', fraudType: 'money_mule' },
    { id: 'TX-00418', clientId: 'C-004', clientName: 'Jovan Đorđević', amount: 150, currency: 'EUR', channel: 'ATM', country: 'RS', time: '21:30', riskScore: 12, riskLevel: 'low', flags: [], status: 'passed' },
    { id: 'TX-00417', clientId: 'C-005', clientName: 'Milena Pavlović', amount: 7500, currency: 'EUR', channel: 'ONLINE', country: 'RS', time: '21:15', riskScore: 55, riskLevel: 'medium', flags: ['ABNORMALNO_VISOKA','OSETLJIV_SEGMENT'], status: 'pending', fraudType: 'app_scam' },
    { id: 'TX-00416', clientId: 'C-006', clientName: 'Stefan Lukić', amount: 45, currency: 'EUR', channel: 'POS', country: 'RS', time: '21:00', riskScore: 8, riskLevel: 'low', flags: [], status: 'passed' },
    { id: 'TX-00415', clientId: 'C-007', clientName: 'Ivana Marković', amount: 14800, currency: 'EUR', channel: 'TRANSFER', country: 'RS', time: '20:45', riskScore: 88, riskLevel: 'critical', flags: ['STRUCTURING','PASS_THROUGH','MULING_OBRAZAC'], status: 'frozen', fraudType: 'money_mule' },
    { id: 'TX-00414', clientId: 'C-008', clientName: 'Nikola Vukovic', amount: 200, currency: 'EUR', channel: 'POS', country: 'RS', time: '20:30', riskScore: 22, riskLevel: 'low', flags: ['NEUOBICAJENO_VREME'], status: 'passed' },
  ];

  private alerts: Alert[] = [
    { id: 'AL-0091', clientId: 'C-001', clientName: 'Marija Nikolić', type: 'account_takeover', riskScore: 100, riskLevel: 'critical', time: '22:27', flags: ['BRUTE_FORCE_POKUSAJ','SUSPICIOUS_LOGIN','IZMENA_KONTAKT_PODATAKA','NOVI_PRIMALAC','ABNORMALNO_VISOKA'], status: 'reviewing', description: 'Account takeover potvrđen — 5 neuspelih prijava, uspešna sa novog uređaja (Holandija), promena email-a, novi primalac, transfer 4.800€' },
    { id: 'AL-0090', clientId: 'C-007', clientName: 'Ivana Marković', type: 'money_mule', riskScore: 88, riskLevel: 'critical', time: '20:45', flags: ['STRUCTURING','MULING_OBRAZAC','PASS_THROUGH','SUMNJIVI_PRIMAOCI'], status: 'new', description: 'Strukturiranje detektovano — 3 transakcije ispod AML praga (14.800€), pass-through obrazac, 12 različitih primalaca u 24h' },
    { id: 'AL-0089', clientId: 'C-005', clientName: 'Milena Pavlović', type: 'app_scam', riskScore: 55, riskLevel: 'medium', time: '21:15', flags: ['PRVI_TRANSFER_KA_PRIMAOCU','ABNORMALNO_VISOKA','OSETLJIV_SEGMENT','OBRAZAC_POD_STRESOM'], status: 'new', description: 'APP scam indikatori — penzioner, transfer 7.500€ ka nepoznatom primaocu, prethodni pregledi hitnih naloga' },
    { id: 'AL-0088', clientId: 'C-003', clientName: 'Ana Stojanović', type: 'money_mule', riskScore: 72, riskLevel: 'high', time: '21:40', flags: ['RIZICNA_LOKACIJA','NEOCEKIVANI_PRILIV','SUMNJIVA_AKTIVACIJA'], status: 'new', description: 'Nalog bio neaktivan 94 dana — odjednom veliki odliv ka Kajmanskim ostrvima' },
  ];

  private clients: Client[] = [
    { id: 'C-001', name: 'Marija Nikolić', segment: 'redovni', age: 45, country: 'RS', avgMonthlyVolume: 2500, riskScore: 100, riskLevel: 'critical', accountOpenDate: '2019-03-15', knownDevices: 2, knownRecipients: 8, onWatchlist: true, flags: ['BRUTE_FORCE_POKUSAJ','SUSPICIOUS_LOGIN','IZMENA_KONTAKT_PODATAKA','NOVI_PRIMALAC'] },
    { id: 'C-002', name: 'Petar Jović', segment: 'redovni', age: 34, country: 'RS', avgMonthlyVolume: 1800, riskScore: 12, riskLevel: 'low', accountOpenDate: '2018-07-22', knownDevices: 3, knownRecipients: 15, onWatchlist: false, flags: [] },
    { id: 'C-003', name: 'Ana Stojanović', segment: 'redovni', age: 29, country: 'RS', avgMonthlyVolume: 3200, riskScore: 72, riskLevel: 'high', accountOpenDate: '2020-11-08', knownDevices: 1, knownRecipients: 3, onWatchlist: true, flags: ['RIZICNA_LOKACIJA','SUMNJIVA_AKTIVACIJA'] },
    { id: 'C-004', name: 'Jovan Đorđević', segment: 'VIP', age: 52, country: 'RS', avgMonthlyVolume: 15000, riskScore: 8, riskLevel: 'low', accountOpenDate: '2012-02-14', knownDevices: 5, knownRecipients: 42, onWatchlist: false, flags: [] },
    { id: 'C-005', name: 'Milena Pavlović', segment: 'penzioner', age: 68, country: 'RS', avgMonthlyVolume: 800, riskScore: 55, riskLevel: 'medium', accountOpenDate: '2015-06-30', knownDevices: 1, knownRecipients: 4, onWatchlist: true, flags: ['OSETLJIV_SEGMENT','OBRAZAC_POD_STRESOM'] },
    { id: 'C-007', name: 'Ivana Marković', segment: 'mladi', age: 22, country: 'RS', avgMonthlyVolume: 600, riskScore: 88, riskLevel: 'critical', accountOpenDate: '2024-01-10', knownDevices: 2, knownRecipients: 28, onWatchlist: true, flags: ['MULING_OBRAZAC','STRUCTURING','PASS_THROUGH'] },
  ];

  private statsData: Stats = {
    totalTransactions: 1847,
    blockedToday: 23,
    activeAlerts: 4,
    criticalAlerts: 2,
    fraudByType: [
      { type: 'Account Takeover', count: 8, color: '#ef4444' },
      { type: 'Money Mule', count: 12, color: '#f59e0b' },
      { type: 'APP Scam', count: 5, color: '#8b5cf6' },
      { type: 'Card Fraud', count: 9, color: '#2d7dd2' },
    ],
    riskDistribution: [
      { level: 'Nizak (0–30)', count: 1698, color: '#22c55e' },
      { level: 'Srednji (30–60)', count: 98, color: '#f59e0b' },
      { level: 'Visok (60–85)', count: 34, color: '#ef4444' },
      { level: 'Kritičan (85+)', count: 17, color: '#dc2626' },
    ],
    hourlyVolume: [
      { hour: '18h', normal: 180, suspicious: 4 },
      { hour: '19h', normal: 210, suspicious: 6 },
      { hour: '20h', normal: 195, suspicious: 11 },
      { hour: '21h', normal: 240, suspicious: 18 },
      { hour: '22h', normal: 160, suspicious: 22 },
      { hour: '23h', normal: 90, suspicious: 14 },
    ]
  };

  transactions$ = new BehaviorSubject<Transaction[]>(this.transactions);
  alerts$ = new BehaviorSubject<Alert[]>(this.alerts);
  clients$ = new BehaviorSubject<Client[]>(this.clients);
  stats$ = new BehaviorSubject<Stats>(this.statsData);

  getTransactions() { return this.transactions$.asObservable(); }
  getAlerts() { return this.alerts$.asObservable(); }
  getClients() { return this.clients$.asObservable(); }
  getStats() { return this.stats$.asObservable(); }

  getClientById(id: string) { return this.clients.find(c => c.id === id); }
  getAlertById(id: string) { return this.alerts.find(a => a.id === id); }

  updateAlertStatus(id: string, status: Alert['status']) {
    const alert = this.alerts.find(a => a.id === id);
    if (alert) {
      alert.status = status;
      this.alerts$.next([...this.alerts]);
    }
  }

  runDiagnostic(hypothesis: 'account_takeover' | 'money_mule' | 'app_scam', clientId: string): DiagnosticResult {
    const client = this.clients.find(c => c.id === clientId);
    if (!client) return { hypothesis, clientId, confirmed: false, confidence: 0, evidenceChain: [], timestamp: new Date().toLocaleTimeString() };

    const chains: Record<string, DiagnosticResult['evidenceChain']> = {
      account_takeover: [
        { label: 'compromisedAccess()', type: 'query', confirmed: client.flags.includes('BRUTE_FORCE_POKUSAJ') || client.flags.includes('SUSPICIOUS_LOGIN'), detail: 'BRUTE_FORCE_POKUSAJ ∨ SUSPICIOUS_LOGIN' },
        { label: 'Flag(BRUTE_FORCE_POKUSAJ)', type: 'fact', confirmed: client.flags.includes('BRUTE_FORCE_POKUSAJ'), detail: '5 neuspelih prijava u 3 min — IP 185.22.x.x (NL)' },
        { label: 'Flag(IZMENA_KONTAKT_PODATAKA)', type: 'fact', confirmed: client.flags.includes('IZMENA_KONTAKT_PODATAKA'), detail: 'Email promenjen u 22:21 — unutar 24h od sumnj. prijave' },
        { label: 'Flag(NOVI_PRIMALAC)', type: 'fact', confirmed: client.flags.includes('NOVI_PRIMALAC'), detail: 'Petar P. — inostrani račun, dodat u 22:25' },
        { label: 'Flag(ABNORMALNO_VISOKA)', type: 'fact', confirmed: client.riskScore > 60, detail: 'Transfer 4.800€ = 1.92× mes. promet (prosek 2.500€)' },
      ],
      money_mule: [
        { label: 'Flag(SUMNJIVA_AKTIVACIJA)', type: 'fact', confirmed: client.flags.includes('SUMNJIVA_AKTIVACIJA'), detail: 'Nalog neaktivan 94 dana → nagli obrt' },
        { label: 'Flag(MULING_OBRAZAC)', type: 'fact', confirmed: client.flags.includes('MULING_OBRAZAC'), detail: '12 primalaca u 24h, avg. iznos < 200€' },
        { label: 'Flag(PASS_THROUGH)', type: 'fact', confirmed: client.flags.includes('PASS_THROUGH'), detail: 'Odliv/Priliv = 0.94 (hist. 0.21)' },
        { label: 'reaches("C-007","dirty_source")', type: 'query', confirmed: client.flags.includes('MULING_OBRAZAC'), detail: 'Rekurzivni query — lanac 3 posrednika do izvornog naloga' },
        { label: 'Flag(STRUCTURING)', type: 'fact', confirmed: client.flags.includes('STRUCTURING'), detail: '3× transakcija od ~14.800€ ispod AML praga 15.000€' },
      ],
      app_scam: [
        { label: 'Flag(PRVI_TRANSFER_KA_PRIMAOCU)', type: 'fact', confirmed: client.flags.includes('PRVI_TRANSFER_KA_PRIMAOCU') || true, detail: 'Primalac nikada ranije nije korišćen' },
        { label: 'Flag(ABNORMALNO_VISOKA)', type: 'fact', confirmed: true, detail: 'Transfer 7.500€ = 9.4× prosečnog iznosa (800€/mes)' },
        { label: 'Flag(OBRAZAC_POD_STRESOM)', type: 'fact', confirmed: client.flags.includes('OBRAZAC_POD_STRESOM'), detail: '3 pregleda limita + 2 neuspela prenosa u 15 min pre transakcije' },
        { label: 'Flag(OSETLJIV_SEGMENT)', type: 'fact', confirmed: client.flags.includes('OSETLJIV_SEGMENT'), detail: 'Segment: penzioner (68 god.) → multiplikator ×1.3' },
      ],
    };

    const chain = chains[hypothesis] || [];
    const confirmed = chain.filter(e => e.confirmed).length >= Math.ceil(chain.length * 0.6);
    const confidence = Math.round((chain.filter(e => e.confirmed).length / chain.length) * 100);

    return { hypothesis, clientId, confirmed, confidence, evidenceChain: chain, timestamp: new Date().toLocaleTimeString() };
  }
}
