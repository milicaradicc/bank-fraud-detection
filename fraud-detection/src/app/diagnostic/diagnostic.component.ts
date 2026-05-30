import { Component, OnInit } from '@angular/core';
import { FraudDataService, Client } from '../services/fraud-data.service';

export interface DiagnosticResult {
  hypothesis: string;
  clientId: string;
  confirmed: boolean;
  confidence: number;
  evidenceChain: { label: string; type: string; confirmed: boolean; detail: string }[];
  timestamp: string;
}

@Component({
  selector: 'app-diagnostic',
  templateUrl: './diagnostic.component.html',
  styleUrls: ['./diagnostic.component.scss']
})
export class DiagnosticComponent implements OnInit {
  clients: Client[] = [];
  selectedHypothesis = 'account_takeover';
  selectedClient = '';
  result: DiagnosticResult | null = null;
  running = false;

  constructor(private fraudData: FraudDataService) {}

  ngOnInit() {
    this.fraudData.getClients().subscribe(data => {
      this.clients = data.map(c => ({ ...c, id: c.clientId }));
      if (this.clients.length > 0) {
        this.selectedClient = this.clients[0].clientId;
      }
    });
  }

  runDiagnostic() {
    this.running = true;
    setTimeout(() => {
      this.result = {
        hypothesis: this.selectedHypothesis,
        clientId: this.selectedClient,
        confirmed: true,
        confidence: 80,
        evidenceChain: [],
        timestamp: new Date().toLocaleTimeString()
      };
      this.running = false;
    }, 800);
  }

  getQueryName() { return this.selectedHypothesis; }
  getQueryCode() { return '// Drools query'; }
  getHypothesisLabel(h: string) { return h; }
  getRecommendation(h: string) { return 'Kontaktirajte klijenta'; }
}