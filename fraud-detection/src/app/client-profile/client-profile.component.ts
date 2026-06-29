import { Component, OnInit } from '@angular/core';
import { FraudDataService, Client } from '../services/fraud-data.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-client-profile',
  templateUrl: './client-profile.component.html',
  styleUrls: ['./client-profile.component.scss']
})
export class ClientProfileComponent implements OnInit {
  clients: Client[] = [];
  private readonly API = environment.apiBaseUrl;

  constructor(private fraudDataService: FraudDataService, private http: HttpClient) {}

  ngOnInit() {
    this.fraudDataService.getClients().subscribe(data => {
      this.clients = data.map(c => ({
        ...c,
        id: c.clientId,
        avgMonthlyVolume: c.averageMonthlyTurnover,
        flags: []
      }));

      // Za svakog klijenta dohvati prave flagove iz sesije
      this.clients.forEach(c => {
        this.http.get<string[]>(`${this.API}/alerts/flags/${c.clientId}`).subscribe({
          next: flags => { c.flags = flags; },
          error: () => { c.flags = []; }
        });
      });
    });
  }
}