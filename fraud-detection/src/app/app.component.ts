import { Component } from '@angular/core';
import { AuthService, AuthUser } from './services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  alertCount = 2;

  constructor(public authService: AuthService, private router: Router) {}

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