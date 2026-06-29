import { Component, OnInit } from '@angular/core';
import { AuthService, AuthUser } from './services/auth.service';
import { FraudDataService } from './services/fraud-data.service';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  alertCount = 0;

  constructor(
    public authService: AuthService,
    private fraudDataService: FraudDataService,
    private router: Router
  ) {}

  ngOnInit() {
    // Osvezi broj pri pokretanju
    this.refreshAlertCount();

    // Osvezi broj pri svakoj promeni rute (npr. kad se vratis na Upozorenja)
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe(() => this.refreshAlertCount());
  }

  refreshAlertCount() {
    // Samo analitcar/admin vide alerte
    if (!this.authService.hasRole('ADMIN', 'ANALYST')) {
      this.alertCount = 0;
      return;
    }
    this.fraudDataService.getAlerts().subscribe({
      next: data => {
        this.alertCount = data.filter(a => !a.status || a.status === 'new').length;
      },
      error: () => { this.alertCount = 0; }
    });
  }

  get currentUser(): AuthUser | null {
    return this.authService.getCurrentUser();
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get roleLabel(): string {
    const map: Record<string, string> = {
      ADMIN: 'Administrator',
      ANALYST: 'Fraud Analyst',
      CLIENT: 'Klijent'
    };
    return this.currentUser ? map[this.currentUser.role] : '';
  }

  logout() {
    this.authService.logout();
  }
}