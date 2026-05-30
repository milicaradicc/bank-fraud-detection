import { Component, OnInit } from '@angular/core';
import { FraudDataService, Client } from '../services/fraud-data.service';

@Component({ selector: 'app-client-profile', templateUrl: './client-profile.component.html', styleUrls: ['./client-profile.component.scss'] })
export class ClientProfileComponent implements OnInit {
  clients: Client[] = [];
  constructor(private fraudData: FraudDataService) {}
  ngOnInit() { this.fraudData.getClients().subscribe(c => this.clients = c); }
}
