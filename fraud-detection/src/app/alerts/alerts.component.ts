import { Component, OnInit } from '@angular/core';
import { FraudDataService, Alert } from '../services/fraud-data.service';

@Component({ selector: 'app-alerts', templateUrl: './alerts.component.html', styleUrls: ['./alerts.component.scss'] })
export class AlertsComponent implements OnInit {
  alerts: Alert[] = [];
  constructor(private fraudData: FraudDataService) {}
  ngOnInit() { this.fraudData.getAlerts().subscribe(a => this.alerts = a); }
  confirm(id: string) { this.fraudData.updateAlertStatus(id, 'confirmed'); }
  review(id: string) { this.fraudData.updateAlertStatus(id, 'reviewing'); }
  dismiss(id: string) { this.fraudData.updateAlertStatus(id, 'dismissed'); }
  getStatusLabel(s: string) { return { new:'Novo', reviewing:'U pregledu', confirmed:'Potvrđeno', dismissed:'Odbačeno' }[s] || s; }
  getFraudTypeLabel(t: string) { return { card_fraud:'Kartična prevara', account_takeover:'Account Takeover', money_mule:'Money Mule', app_scam:'APP Scam' }[t] || t; }
}
