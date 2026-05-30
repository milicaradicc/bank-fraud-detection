import { Component, OnInit } from '@angular/core';
import { FraudDataService, Transaction, Stats } from '../services/fraud-data.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: Stats | null = null;
  recentTransactions: Transaction[] = [];
  today = new Date().toLocaleDateString('sr');

  constructor(private fraudData: FraudDataService) {}

  ngOnInit() {
    this.fraudData.getStats().subscribe(s => this.stats = s);
    this.fraudData.getTransactions().subscribe(data => {
      this.recentTransactions = data.map(tx => ({
        ...tx,
        id: tx.transactionId,
        time: new Date(tx.timestamp).toLocaleTimeString('sr', { hour: '2-digit', minute: '2-digit' }),
        clientName: tx.clientId,
        riskScore: tx.riskScore || 0,
        riskLevel: tx.riskLevel || 'LOW',
        flags: [],
        status: 'passed'
      })).slice(0, 8);
    });
  }
}