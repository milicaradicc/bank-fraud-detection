import { Component, OnInit } from '@angular/core';
import { FraudDataService, Stats } from '../services/fraud-data.service';

@Component({ selector: 'app-analytics', templateUrl: './analytics.component.html', styleUrls: ['./analytics.component.scss'] })
export class AnalyticsComponent implements OnInit {
  stats: Stats | null = null;
  cepRules = [
    { name: 'velocity_card_testing', window: '60 sekundi', minCount: 5, fires: 3 },
    { name: 'failed_login_burst', window: '5 minuta', minCount: 5, fires: 7 },
    { name: 'impossible_travel', window: '6 sati', minCount: 2, fires: 1 },
    { name: 'structuring_aml', window: '48 sati', minCount: 3, fires: 2 },
    { name: 'pass_through', window: '1 sat', minCount: 1, fires: 4 },
    { name: 'burst_after_idle', window: '24 sata', minCount: 3, fires: 2 },
  ];
  templates = [
    { name: 'risk_thresholds', rules: 4, desc: 'Pragovi rizika po segmentima (VIP/redovni/mladi/penzioner)', params: ['segment','alertCut','blockCut','freezeCut'] },
    { name: 'country_lists', rules: 8, desc: 'Crna i siva lista jurisdikcija (FATF)', params: ['countryCode','listType','weight'] },
    { name: 'high_risk_mcc', rules: 4, desc: 'Visokorizični MCC kodovi merchant-a', params: ['mcc','weight'] },
    { name: 'cep_windows', rules: 6, desc: 'Vremenski prozori CEP pravila', params: ['ruleName','windowLen','minCount','weight'] },
    { name: 'aml_thresholds', rules: 4, desc: 'AML pragovi po jurisdikciji', params: ['jurisdiction','reportThreshold','nearThreshold'] },
  ];
  constructor(private fraudData: FraudDataService) {}
  ngOnInit() { this.fraudData.getStats().subscribe(s => this.stats = s); }
}
