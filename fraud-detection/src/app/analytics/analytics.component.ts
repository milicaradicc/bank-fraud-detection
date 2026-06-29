import { Component, OnInit } from '@angular/core';
import { FraudDataService, Stats } from '../services/fraud-data.service';

@Component({ selector: 'app-analytics', templateUrl: './analytics.component.html', styleUrls: ['./analytics.component.scss'] })
export class AnalyticsComponent implements OnInit {
  stats: Stats | null = null;

  // CEP pravila koja postoje u sistemu (rules/cep paket)
  cepRules = [
    { name: 'velocity_card_testing', window: '60 sekundi', minCount: 5 },
    { name: 'failed_login_burst', window: '5 minuta', minCount: 5 },
    { name: 'impossible_travel', window: '6 sati', minCount: 2 },
    { name: 'structuring_aml', window: '48 sati', minCount: 3 },
    { name: 'pass_through', window: '1 sat', minCount: 1 },
    { name: 'burst_after_idle', window: '24 sata', minCount: 3 },
  ];

  templates = [
    { name: 'risk_thresholds', rules: 4, desc: 'Pragovi rizika po segmentima (VIP/redovni/mladi/penzioner)', params: ['segment','alertCut','blockCut','freezeCut'] },
    { name: 'mcc_risk', rules: 5, desc: 'Visokorizični MCC kodovi merchant-a', params: ['mcc','category','weight'] },
    { name: 'country_lists', rules: 20, desc: 'Crna i siva lista jurisdikcija (FATF) — origin i recipient', params: ['countryCode','listType','weight'] },
  ];

  constructor(private fraudData: FraudDataService) {}
  ngOnInit() { this.fraudData.getStats().subscribe(s => this.stats = s); }
}