import { Component, OnInit } from '@angular/core';
import { FraudDataService, Transaction } from '../services/fraud-data.service';
import { HttpClient } from '@angular/common/http';

export interface TransactionResult {
  transactionId: string;
  clientId: string;
  amount: number;
  riskScore: number;
  riskLevel: string;
  action: string;
  message: string;
  triggeredFlags: string[];
  contributingFlags: string[];
  multiplierReason: string;
}

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
  showForm = false;
  result: TransactionResult | null = null;
  newTx = {
    clientId: 'C-001',
    amount: 0,
    currency: 'EUR',
    channel: 'TRANSFER',
    country: 'RS',
    deviceId: '',
    recipientId: '',
    recipientIsNew: false,
    recipientIsForeignAccount: false,
    recipientCountry: '',
    inflow: false,
    mccCode: 0
  };

  constructor(
      private fraudDataService: FraudDataService,
      private http: HttpClient
    ) {}
    
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

  submitTransaction() {
    this.http.post<TransactionResult>(
      'http://localhost:8080/api/transactions', this.newTx
    ).subscribe({
      next: res => {
        this.result = res;
        this.fraudDataService.getTransactions().subscribe(data => {
          this.transactions = data;
          this.filtered = data;
        });
      },
      error: err => console.error(err)
    });
  }
}