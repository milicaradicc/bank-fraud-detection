import { Component, OnInit } from '@angular/core';
import { FraudDataService, Client } from '../services/fraud-data.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface EvidenceItem {
  flagType: string;
  description: string;
  timestamp: string;
  critical: boolean;
}

export interface DiagnosticResult {
  hypothesis: string;
  clientId: string;
  confirmed: boolean;
  evidenceChain: EvidenceItem[];
  explanation: string;
  timestamp: string;
  muleNetwork?: string[];   // lanac naloga za money mule
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
  customClientId = '';
  useCustomId = false;
  result: DiagnosticResult | null = null;
  running = false;
  error = '';

  private readonly API = `${environment.apiBaseUrl}/diagnostic`;

  constructor(private fraudData: FraudDataService, private http: HttpClient) {}

  ngOnInit() {
    this.fraudData.getClients().subscribe(data => {
      this.clients = data.map(c => ({ ...c, id: c.clientId }));
      if (this.clients.length > 0) {
        this.selectedClient = this.clients[0].clientId;
      }
    });
  }

  get effectiveClientId(): string {
    return this.useCustomId ? this.customClientId : this.selectedClient;
  }

  runDiagnostic() {
    const clientId = this.effectiveClientId;
    if (!clientId) return;

    this.running = true;
    this.error = '';
    this.result = null;

    // Money mule ide na poseban endpoint (vraca listu naloga, ne flagove)
    if (this.selectedHypothesis === 'money_mule') {
      this.runMoneyMule(clientId);
      return;
    }

    const endpoint = this.selectedHypothesis === 'account_takeover'
      ? `${this.API}/account-takeover/${clientId}`
      : `${this.API}/app-scam/${clientId}`;

    this.http.get<any>(endpoint).subscribe({
      next: res => {
        this.result = {
          hypothesis: res.hypothesis,
          clientId: res.clientId,
          confirmed: res.confirmed,
          evidenceChain: res.evidenceChain || [],
          explanation: res.explanation,
          timestamp: new Date().toLocaleTimeString('sr')
        };
        this.running = false;
      },
      error: () => {
        this.error = 'Greška pri pozivu dijagnostike. Proveri da li klijent postoji.';
        this.running = false;
      }
    });
  }

  private runMoneyMule(clientId: string) {
    const endpoint = `${this.API}/money-mule/network/${clientId}`;
    this.http.get<string[]>(endpoint).subscribe({
      next: network => {
        const confirmed = network && network.length > 0;
        this.result = {
          hypothesis: 'MONEY_MULE',
          clientId: clientId,
          confirmed: confirmed,
          evidenceChain: [],
          muleNetwork: network || [],
          explanation: confirmed
            ? `Rekurzivni upit reaches() je pronašao lanac prosleđivanja novca. Novac sa naloga ${clientId} stiže do ${network.length} posrednika kroz proizvoljno mnogo koraka.`
            : `Rekurzivni upit reaches() nije pronašao nijedan lanac prosleđivanja sa naloga ${clientId}. Klijent nije izvor money mule mreže.`,
          timestamp: new Date().toLocaleTimeString('sr')
        };
        this.running = false;
      },
      error: () => {
        this.error = 'Greška pri pozivu money mule dijagnostike. Proveri da li klijent postoji.';
        this.running = false;
      }
    });
  }

  getQueryName() {
    if (this.selectedHypothesis === 'account_takeover') return 'accountTakeover';
    if (this.selectedHypothesis === 'money_mule') return 'reaches';
    return 'appScam';
  }

  getQueryCode() {
    if (this.selectedHypothesis === 'account_takeover') {
      return `query compromisedAccess(String acc)
    Flag(clientId == acc, type == FlagType.BRUTE_FORCE_POKUSAJ)
    or
    Flag(clientId == acc, type == FlagType.SUSPICIOUS_LOGIN)
end

query accountTakeover(String acc)
    compromisedAccess(acc;)
    ( Flag(clientId == acc, type == FlagType.IZMENA_KONTAKT_PODATAKA)
      or Flag(clientId == acc, type == FlagType.NOVI_PRIMALAC) )
    Flag(clientId == acc, type == FlagType.ABNORMALNO_VISOKA)
end`;
    }
    if (this.selectedHypothesis === 'money_mule') {
      return `query reaches(String from, String to)
    // baza rekurzije: direktan odliv
    Transaction(clientId == from, recipientId == to, inflow == false)
    or
    // rekurzivni korak: preko medjucvora $z
    ( Transaction(clientId == from, $z : recipientId, inflow == false)
      and reaches($z, to;) )
end`;
    }
    return `query appScam(String acc)
    Flag(clientId == acc, type == FlagType.PRVI_TRANSFER_KA_PRIMAOCU)
    Flag(clientId == acc, type == FlagType.ABNORMALNO_VISOKA)
    ( Flag(clientId == acc, type == FlagType.OBRAZAC_POD_STRESOM)
      or Flag(clientId == acc, type == FlagType.OSETLJIV_SEGMENT) )
end`;
  }

  getHypothesisLabel(h: string) {
    const map: Record<string, string> = {
      'ACCOUNT_TAKEOVER': 'Account Takeover',
      'APP_SCAM': 'APP Scam',
      'MONEY_MULE': 'Money Mule'
    };
    return map[h] || h;
  }

  getRecommendation(h: string) {
    if (h === 'ACCOUNT_TAKEOVER') {
      return 'Kontaktirajte klijenta telefonskim pozivom, vratite lozinku, uklonite kompromitovanog primaoca, prijavite slučaj.';
    }
    if (h === 'APP_SCAM') {
      return 'Pokrenite cooling-off mehanizam — odložite transfer 24h i kontaktirajte klijenta da proverite da li je pod uticajem prevare.';
    }
    if (h === 'MONEY_MULE') {
      return 'Prijavite slučaj AML službi, zamrznite naloge u lancu prosleđivanja i pokrenite istragu nad celom mrežom posrednika.';
    }
    return 'Pregledajte slučaj ručno.';
  }
}