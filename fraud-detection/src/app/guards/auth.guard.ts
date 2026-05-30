import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService, UserRole } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    const requiredRoles = route.data['roles'] as UserRole[] | undefined;
    if (requiredRoles && !this.authService.hasRole(...requiredRoles)) {
      this.router.navigate(['/dashboard']); // nema pristup → back na dashboard
      return false;
    }

    return true;
  }
}