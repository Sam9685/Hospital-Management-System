// frontend/hospital-management-frontend/src/app/features/payments/payments.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="payments-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Payments</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Payment processing feature coming soon...</p>
          <button mat-raised-button color="primary">
            <mat-icon>payment</mat-icon>
            Make Payment
          </button>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .payments-container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
  `]
})
export class PaymentsComponent {}
