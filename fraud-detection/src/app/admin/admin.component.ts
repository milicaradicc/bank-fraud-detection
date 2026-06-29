import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  private readonly API = environment.apiBaseUrl;

  riskRows: any[] = [];
  mccRows: any[] = [];
  countryRows: any[] = [];

  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.http.get<any[]>(`${this.API}/admin/templates/risk-thresholds`).subscribe({
      next: d => this.riskRows = d, error: () => this.riskRows = []
    });
    this.http.get<any[]>(`${this.API}/admin/templates/mcc-risk`).subscribe({
      next: d => this.mccRows = d, error: () => this.mccRows = []
    });
    this.http.get<any[]>(`${this.API}/admin/templates/country-lists`).subscribe({
      next: d => this.countryRows = d, error: () => this.countryRows = []
    });
  }

  saveRisk() {
    this.http.post(`${this.API}/admin/templates/risk-thresholds`, this.riskRows, { responseType: 'text' })
      .subscribe({ next: m => this.showMsg(m, 'success'), error: () => this.showMsg('Greška pri čuvanju.', 'error') });
  }

  saveMcc() {
    this.http.post(`${this.API}/admin/templates/mcc-risk`, this.mccRows, { responseType: 'text' })
      .subscribe({ next: m => this.showMsg(m, 'success'), error: () => this.showMsg('Greška pri čuvanju.', 'error') });
  }

  saveCountry() {
    this.http.post(`${this.API}/admin/templates/country-lists`, this.countryRows, { responseType: 'text' })
      .subscribe({ next: m => this.showMsg(m, 'success'), error: () => this.showMsg('Greška pri čuvanju.', 'error') });
  }

  addMccRow() {
    this.mccRows.push({ mcc: 0, category: '', weight: 0 });
  }
  removeMccRow(i: number) {
    this.mccRows.splice(i, 1);
  }

  addCountryRow() {
    this.countryRows.push({ countryCode: '', listType: 'siva', weight: 15 });
  }
  removeCountryRow(i: number) {
    this.countryRows.splice(i, 1);
  }

  private showMsg(m: string, type: 'success' | 'error') {
    this.message = m;
    this.messageType = type;
    setTimeout(() => { this.message = ''; this.messageType = ''; }, 6000);
  }
}