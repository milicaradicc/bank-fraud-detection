import { Component, OnInit } from '@angular/core';
import { FraudDataService, Transaction } from '../services/fraud-data.service';
import { AuthService } from '../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { DeviceFingerprintService } from '../services/device-fingerprint.service';

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
  requiresStepUp: boolean;
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
  isClient = false;

  stepUpCode = '';
  stepUpStatus: 'idle' | 'confirming' | 'success' | 'error' = 'idle';
  stepUpMessage = '';

  newTx = {
    clientId: '',
    amount: 0,
    currency: 'EUR',
    channel: 'TRANSFER',
    country: 'RS',
    city: '',
    deviceId: '',
    recipientId: '',
    recipientCountry: '',
    mccCode: 0
  };

  constructor(
    private fraudDataService: FraudDataService,
    private authService: AuthService,
    private http: HttpClient,
    private deviceFingerprint: DeviceFingerprintService,
  ) {}

  ngOnInit() {
    this.isClient = this.authService.isClient();
    this.newTx.deviceId = this.deviceFingerprint.getDeviceId();
    this.loadTransactions();
  }

  loadTransactions() {
    const endpoint = this.isClient
      ? 'http://localhost:8080/api/transactions/my'
      : 'http://localhost:8080/api/transactions';

    this.http.get<Transaction[]>(endpoint).subscribe(data => {
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
    this.stepUpStatus = 'idle';
    this.stepUpCode = '';

    this.http.post<TransactionResult>(
      'http://localhost:8080/api/transactions', this.newTx
    ).subscribe({
      next: res => {
        this.result = res;
        this.loadTransactions();
        this.showForm = false;
      },
      error: err => console.error(err)
    });
  }

  confirmStepUp() {
    if (!this.result || !this.stepUpCode) return;
    this.stepUpStatus = 'confirming';

    this.http.post<{ confirmed: boolean; message: string }>(
      `http://localhost:8080/api/transactions/${this.result.transactionId}/confirm`,
      { code: this.stepUpCode }
    ).subscribe({
      next: res => {
        this.stepUpStatus = res.confirmed ? 'success' : 'error';
        this.stepUpMessage = res.message;
      },
      error: () => {
        this.stepUpStatus = 'error';
        this.stepUpMessage = 'Greška pri potvrdi. Pokušajte ponovo.';
      }
    });
  }

  resetForm() {
    this.newTx = {
      clientId: '',
      amount: 0,
      currency: 'EUR',
      channel: 'TRANSFER',
      country: 'RS',
      city: '',
      deviceId: this.deviceFingerprint.getDeviceId(),
      recipientId: '',
      recipientCountry: '',
      mccCode: 0
    };
    this.result = null;
    this.stepUpStatus = 'idle';
    this.stepUpCode = '';
  }
}