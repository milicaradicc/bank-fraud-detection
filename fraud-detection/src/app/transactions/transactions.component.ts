import { Component, OnInit } from '@angular/core';
import { FraudDataService, Transaction } from '../services/fraud-data.service';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[] = [];
  filtered: Transaction[] = [];
  searchTerm = '';
  filterChannel = '';

  constructor(private fraudDataService: FraudDataService) {}

  ngOnInit() {
    this.fraudDataService.getTransactions().subscribe(data => {
      this.transactions = data;
      this.filtered = data;
    });
  }

  applyFilters() {
    this.filtered = this.transactions.filter(tx => {
      const matchSearch = !this.searchTerm ||
        tx.clientId.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        tx.transactionId.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchChannel = !this.filterChannel || tx.channel === this.filterChannel;
      return matchSearch && matchChannel;
    });
  }
}