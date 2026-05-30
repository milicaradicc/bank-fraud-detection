import { Component, OnInit } from '@angular/core';
import { FraudDataService, Alert } from '../services/fraud-data.service';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss']
})
export class AlertsComponent implements OnInit {
  alerts: Alert[] = [];

  constructor(private fraudDataService: FraudDataService) {}

  ngOnInit() {
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

  confirm(id: string) {}
  review(id: string) {}
  dismiss(id: string) {}
}