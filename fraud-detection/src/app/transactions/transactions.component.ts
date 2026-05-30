import { Component, OnInit } from '@angular/core';
import { FraudDataService, Transaction } from '../services/fraud-data.service';

@Component({ selector: 'app-transactions', templateUrl: './transactions.component.html', styleUrls: ['./transactions.component.scss'] })
export class TransactionsComponent implements OnInit {
  all: Transaction[] = [];
  filtered: Transaction[] = [];
  searchTerm = ''; filterRisk = ''; filterChannel = ''; filterStatus = '';

  constructor(private fraudData: FraudDataService) {}
  ngOnInit() { this.fraudData.getTransactions().subscribe(t => { this.all = t; this.applyFilters(); }); }
  applyFilters() {
    this.filtered = this.all.filter(t =>
      (!this.searchTerm || t.clientName.toLowerCase().includes(this.searchTerm.toLowerCase()) || t.id.includes(this.searchTerm)) &&
      (!this.filterRisk || t.riskLevel === this.filterRisk) &&
      (!this.filterChannel || t.channel === this.filterChannel) &&
      (!this.filterStatus || t.status === this.filterStatus)
    );
  }
}
