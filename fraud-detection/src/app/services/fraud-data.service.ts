import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type ClientSegment = 'VIP' | 'REGULAR' | 'YOUNG' | 'PENSIONER';

export interface Client {
  clientId: string;
  id: string;                // ukloni ?
  name: string;
  age: number;
  segment: ClientSegment;
  accountOpenedDate: string;
  countryOfResidence: string;
  averageMonthlyTurnover: number;
  averageTransactionAmount: number;
  onWatchlist: boolean;
  riskScore: number;         
  riskLevel: RiskLevel;     
  knownDevices: string[];
  knownRecipients: string[];
  blockedTransactionsLast7Days: number;
  hasPreviousFraudCase: boolean;
  kycFailed: boolean;
  avgMonthlyVolume: number;  
  flags: string[];           
}

export interface Transaction {
  transactionId: string;
  clientId: string;
  clientName: string;        
  amount: number;
  currency: string;
  timestamp: string;
  time: string;              
  channel: 'POS' | 'ONLINE' | 'ATM' | 'TRANSFER';
  country: string;
  city?: string;
  deviceId?: string;
  recipientId?: string;
  recipientIsNew: boolean;
  recipientIsForeignAccount: boolean;
  recipientCountry?: string;
  inflow: boolean;
  merchantCategory?: string;
  mccCode?: number;
  riskScore: number;         
  riskLevel: RiskLevel;      
  flags: string[];           
  status: string;            
  id: string;                
}

export interface Alert {
  clientId: string;
  clientName: string;       
  transactionId: string;
  action: string;
  riskLevel: RiskLevel;
  score: number;
  timestamp: string;
  message: string;
  description: string;       
  escalatedToAnalyst: boolean;
  status: 'new' | 'reviewing' | 'confirmed' | 'dismissed';  
  flags: string[];           
  type: string;              
  time: string;              
  id: string;                
  riskScore: number;         
}

export interface Stats {
  totalTransactions: number;
  blockedToday: number;
  activeAlerts: number;
  criticalAlerts: number;
  fraudByType: { type: string; count: number; color: string }[];
  riskDistribution: { level: string; count: number; color: string }[];
}

@Injectable({ providedIn: 'root' })
export class FraudDataService {

  private readonly API = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.API}/clients`);
  }

  getClient(clientId: string): Observable<Client> {
    return this.http.get<Client>(`${this.API}/clients/${clientId}`);
  }

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.API}/transactions`);
  }

  getTransactionsByClient(clientId: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.API}/transactions/client/${clientId}`);
  }

  getAlerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.API}/alerts`);
  }

  getAlertsByClient(clientId: string): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.API}/alerts/client/${clientId}`);
  }

  getStats(): Observable<Stats> {
    return this.http.get<Stats>(`${this.API}/stats`);
  }
}