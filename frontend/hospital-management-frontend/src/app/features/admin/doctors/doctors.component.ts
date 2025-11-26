// frontend/hospital-management-frontend/src/app/features/admin/doctors/doctors.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-doctors',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="doctors-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Manage Doctors</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Doctor management feature coming soon...</p>
          <button mat-raised-button color="primary">
            <mat-icon>add</mat-icon>
            Add Doctor
          </button>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .doctors-container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
  `]
})
export class DoctorsComponent {}
