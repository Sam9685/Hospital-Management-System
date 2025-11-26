// frontend/hospital-management-frontend/src/app/features/admin/admin-dashboard/admin-dashboard.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="admin-dashboard-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Admin Dashboard</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Admin dashboard feature coming soon...</p>
          <div class="admin-actions">
            <button mat-raised-button color="primary" routerLink="/admin/doctors">
              <mat-icon>people</mat-icon>
              Manage Doctors
            </button>
            <button mat-raised-button color="primary" routerLink="/admin/appointments">
              <mat-icon>event</mat-icon>
              Manage Appointments
            </button>
            <button mat-raised-button color="primary" routerLink="/admin/complaints">
              <mat-icon>feedback</mat-icon>
              Manage Complaints
            </button>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .admin-dashboard-container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
    .admin-actions {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
    }
  `]
})
export class AdminDashboardComponent {}
