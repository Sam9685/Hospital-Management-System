// frontend/hospital-management-frontend/src/app/features/appointments/appointments.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="appointments-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>My Appointments</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Appointment management feature coming soon...</p>
          <button mat-raised-button color="primary" routerLink="/book-appointment">
            <mat-icon>add</mat-icon>
            Book New Appointment
          </button>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .appointments-container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
  `]
})
export class AppointmentsComponent {}
