import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

export type UserRole = 'ADMIN' | 'ANALYST' | 'CLIENT';

export interface AuthUser {
  username: string;
  role: UserRole;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'fg_token';

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string) {
    return this.http.post<{ token: string }>(`${this.API}/login`, { username, password })
      .pipe(tap(res => localStorage.setItem(this.TOKEN_KEY, res.token)));
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    // Proveri expiry iz JWT payload-a
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  getCurrentUser(): AuthUser | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return { username: payload.sub, role: payload.role as UserRole };
    } catch {
      return null;
    }
  }

  hasRole(...roles: UserRole[]): boolean {
    const user = this.getCurrentUser();
    return user ? roles.includes(user.role) : false;
  }
}