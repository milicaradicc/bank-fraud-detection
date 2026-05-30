import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.username || !this.password) return;
    this.loading = true;
    this.error = '';

    this.authService.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.error = 'Pogrešno korisničko ime ili lozinka';
        this.loading = false;
      }
    });
  }
}