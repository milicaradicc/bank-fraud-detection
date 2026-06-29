import { Component, OnInit } from '@angular/core';
import { FraudDataService, Alert } from '../services/fraud-data.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss']
})
export class AlertsComponent implements OnInit {
  alerts: Alert[] = [];
  private readonly API = environment.apiBaseUrl;

  constructor(private fraudDataService: FraudDataService, private http: HttpClient) {}

  ngOnInit() {
    this.loadAlerts();
  }

  loadAlerts() {
    this.fraudDataService.getAlerts().subscribe(data => {
      this.alerts = data.map(a => ({
        ...a,
        id: a.transactionId,
        clientName: a.clientId,
        time: new Date(a.timestamp).toLocaleTimeString('sr', { hour: '2-digit', minute: '2-digit' }),
        type: a.action,
        status: (a.status || 'new') as 'new' | 'reviewing' | 'confirmed' | 'dismissed',
        flags: a.flags || [],
        description: a.message,
        riskScore: a.score
      }));
    });
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      new: 'Novo', reviewing: 'U pregledu',
      confirmed: 'Potvrđeno', dismissed: 'Odbačeno'
    };
    return map[status] || status;
  }

  getFraudTypeLabel(type: string): string {
    const map: Record<string, string> = {
      BLOCK: 'Blokada', FREEZE_ACCOUNT: 'Zamrzavanje',
      ALERT_ANALYST: 'Alert', AML_REPORT: 'AML',
      BLOCK_STEPUP: 'Step-up', LOG_ONLY: 'Log'
    };
    return map[type] || type;
  }

  confirm(id: string) {
    this.http.post<Alert>(`${this.API}/alerts/${id}/confirm`, {}).subscribe({
      next: () => {
        const a = this.alerts.find(x => x.id === id);
        if (a) a.status = 'confirmed';
      },
      error: err => console.error('Greška pri potvrdi:', err)
    });
  }

  review(id: string) {
    const a = this.alerts.find(x => x.id === id);
    if (a) a.status = 'reviewing';
  }

  dismiss(id: string) {
    this.http.post<Alert>(`${this.API}/alerts/${id}/dismiss`, {}).subscribe({
      next: () => {
        const a = this.alerts.find(x => x.id === id);
        if (a) a.status = 'dismissed';
      },
      error: err => console.error('Greška pri odbacivanju:', err)
    });
  }
}