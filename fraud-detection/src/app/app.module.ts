import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TransactionsComponent } from './transactions/transactions.component';
import { AlertsComponent } from './alerts/alerts.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { ClientProfileComponent } from './client-profile/client-profile.component';
import { DiagnosticComponent } from './diagnostic/diagnostic.component';
import { FraudDataService } from './services/fraud-data.service';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent, 
    DashboardComponent, 
    TransactionsComponent, 
    AlertsComponent, 
    AnalyticsComponent, 
    ClientProfileComponent, 
    DiagnosticComponent,
    LoginComponent],
  imports: [
    BrowserModule, 
    FormsModule, 
    AppRoutingModule,
    HttpClientModule],
  providers: [FraudDataService],
  bootstrap: [AppComponent]
})
export class AppModule {}
