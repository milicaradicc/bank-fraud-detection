import { Component, OnInit } from '@angular/core';
import { FraudDataService, Client, DiagnosticResult } from '../services/fraud-data.service';

@Component({ selector: 'app-diagnostic', templateUrl: './diagnostic.component.html', styleUrls: ['./diagnostic.component.scss'] })
export class DiagnosticComponent implements OnInit {
  clients: Client[] = [];
  selectedHypothesis: 'account_takeover' | 'money_mule' | 'app_scam' = 'account_takeover';
  selectedClient = 'C-001';
  result: DiagnosticResult | null = null;
  running = false;

  constructor(private fraudData: FraudDataService) {}
  ngOnInit() { this.fraudData.getClients().subscribe(c => this.clients = c); }

  runDiagnostic() {
    this.running = true; this.result = null;
    setTimeout(() => {
      this.result = this.fraudData.runDiagnostic(this.selectedHypothesis, this.selectedClient);
      this.running = false;
    }, 900);
  }

  getQueryName() { return { account_takeover:'accountTakeover', money_mule:'reaches', app_scam:'appScam' }[this.selectedHypothesis]; }
  getHypothesisLabel(h: string) { return { account_takeover:'Account Takeover', money_mule:'Money Mule', app_scam:'APP Scam' }[h] || h; }
  getRecommendation(h: string) { return { account_takeover:'Zamrzni nalog, kontaktiraj klijenta telefonom (ne SMS-om), resetuj lozinku, ukloni kompromitovane primaoce. Prijavi kao ATO.', money_mule:'Prijavi AML službi sa punim lancem transfera. Privremeno zamrzni nalog do istrage. Obavesti druge banke u lancu.', app_scam:'Aktiviraj cooling-off period od 24h. Pozovi klijenta radi verifikacije. Pokreni edukativnu komunikaciju.' }[h] || ''; }
  getQueryCode() {
    const codes: Record<string, string> = {
      account_takeover: `query accountTakeover( String acc )
  compromisedAccess( acc; )  // (a) brute force ∨ sumnjiv login
  ( Flag( clientId == acc,
    type == IZMENA_KONTAKT_PODATAKA )
   or Flag( clientId == acc,
    type == NOVI_PRIMALAC ) )  // (b ∨ c)
  Flag( clientId == acc,
    type == ABNORMALNO_VISOKA )  // (d)
end`,
      money_mule: `query reaches( String from, String to )
  // baza rekurzije
  Transaction( clientId == from,
    recipientId == to, inflow == false )
  or
  // rekurzivni korak
  ( Transaction( clientId == from,
      $z : recipientId, inflow == false )
    and reaches( $z, to; ) )
end`,
      app_scam: `query appScam( String acc )
  Flag( clientId == acc,
    type == PRVI_TRANSFER_KA_PRIMAOCU ) // (a)
  Flag( clientId == acc,
    type == ABNORMALNO_VISOKA )         // (b)
  ( Flag( clientId == acc,
    type == OBRAZAC_POD_STRESOM )       // (c)
   or Flag( clientId == acc,
    type == OSETLJIV_SEGMENT ) )        // (d)
end`
    };
    return codes[this.selectedHypothesis] || '';
  }
}
