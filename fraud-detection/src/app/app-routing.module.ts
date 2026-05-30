import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TransactionsComponent } from './transactions/transactions.component';
import { AlertsComponent } from './alerts/alerts.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { ClientProfileComponent } from './client-profile/client-profile.component';
import { DiagnosticComponent } from './diagnostic/diagnostic.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'transactions',
    component: TransactionsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'alerts',
    component: AlertsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'ANALYST'] }
  },
  {
    path: 'analytics',
    component: AnalyticsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'ANALYST'] }
  },
  {
    path: 'clients',
    component: ClientProfileComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'ANALYST'] }
  },
  {
    path: 'diagnostic',
    component: DiagnosticComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'ANALYST'] }
  },
];

@NgModule({ imports: [RouterModule.forRoot(routes)], exports: [RouterModule] })
export class AppRoutingModule {}