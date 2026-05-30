import { Component, OnInit } from '@angular/core';
import { FraudDataService, Client } from '../services/fraud-data.service';

@Component({
  selector: 'app-client-profile',
  templateUrl: './client-profile.component.html',
  styleUrls: ['./client-profile.component.scss']
})
export class ClientProfileComponent implements OnInit {
  clients: Client[] = [];

  constructor(private fraudDataService: FraudDataService) {}

  ngOnInit() {
    this.fraudDataService.getClients().subscribe(data => {
      this.clients = data.map(c => ({
        ...c,
        id: c.clientId,
        avgMonthlyVolume: c.averageMonthlyTurnover,
        flags: []
      }));
    });
  }
}