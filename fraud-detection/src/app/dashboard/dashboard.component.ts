import { Component, OnInit } from '@angular/core';
import { FraudDataService, Transaction, Stats } from '../services/fraud-data.service';

@Component({ selector: 'app-dashboard', templateUrl: './dashboard.component.html', styleUrls: ['./dashboard.component.scss'] })
export class DashboardComponent implements OnInit {
  stats: Stats | null = null;
  recentTransactions: Transaction[] = [];
  today = new Date().toLocaleDateString('sr-RS', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

  constructor(private fraudData: FraudDataService) {}
  ngOnInit() {
    this.fraudData.getStats().subscribe(s => this.stats = s);
    this.fraudData.getTransactions().subscribe(t => this.recentTransactions = t.slice(0, 6));
  }
}
