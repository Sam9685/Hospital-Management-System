import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-payment-select',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="payment-select-container">
      <div class="payment-header">
        <div class="back-button" (click)="goBack()">
          <svg class="back-icon" viewBox="0 0 24 24">
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
          </svg>
          Back to Appointment
        </div>
        <h1 class="page-title">Choose Payment Method</h1>
        <p class="page-subtitle">Select your preferred payment method to complete your appointment booking</p>
      </div>

      <div class="appointment-summary">
        <h3>Appointment Summary</h3>
        <div class="summary-card">
          <div class="summary-item">
            <span class="label">Doctor:</span>
            <span class="value">{{ appointmentData?.doctorName }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Specialization:</span>
            <span class="value">{{ appointmentData?.specialization }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Date:</span>
            <span class="value">{{ formatDate(appointmentData?.appointmentDate) }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Time:</span>
            <span class="value">{{ formatTime(appointmentData?.appointmentTime) }} - {{ formatTime(appointmentData?.endTime) }}</span>
          </div>
          <div class="summary-item total">
            <span class="label">Total Amount:</span>
            <span class="value">₹{{ appointmentData?.consultationFee }}</span>
          </div>
        </div>
      </div>

      <div class="payment-methods">
        <h3>Select Payment Method</h3>
        <div class="methods-grid">
          <div class="payment-method-card" (click)="selectPaymentMethod('UPI')" [class.selected]="selectedMethod === 'UPI'">
            <div class="method-icon upi">
              <svg viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <div class="method-info">
              <h4>UPI Payment</h4>
              <p>Pay using UPI ID or QR Code</p>
            </div>
            <div class="method-check">
              <svg *ngIf="selectedMethod === 'UPI'" class="check-icon" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            </div>
          </div>

          <div class="payment-method-card" (click)="selectPaymentMethod('CARD')" [class.selected]="selectedMethod === 'CARD'">
            <div class="method-icon card">
              <svg viewBox="0 0 24 24">
                <path d="M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zm0 14H4v-6h16v6zm0-10H4V6h16v2z"/>
              </svg>
            </div>
            <div class="method-info">
              <h4>Credit/Debit Card</h4>
              <p>Pay using your card details</p>
            </div>
            <div class="method-check">
              <svg *ngIf="selectedMethod === 'CARD'" class="check-icon" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <div class="payment-actions">
        <button class="btn btn-secondary" (click)="goBack()">
          Cancel
        </button>
        <button class="btn btn-primary" 
                [disabled]="!selectedMethod" 
                (click)="proceedToPayment()">
          Continue to Payment
          <svg class="btn-icon" viewBox="0 0 24 24">
            <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
          </svg>
        </button>
      </div>
    </div>
  `,
  styleUrls: ['./payment-select.component.css']
})
export class PaymentSelectComponent implements OnInit {
  selectedMethod: string = '';
  appointmentData: any = null;
  currentUser: User | null = null;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAppointmentData();
    this.loadCurrentUser();
  }

  loadAppointmentData(): void {
    const data = sessionStorage.getItem('pendingAppointment');
    if (data) {
      this.appointmentData = JSON.parse(data);
    } else {
      this.router.navigate(['/appointments/schedule']);
    }
  }

  loadCurrentUser(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
    }
  }

  selectPaymentMethod(method: string): void {
    this.selectedMethod = method;
  }

  proceedToPayment(): void {
    if (this.selectedMethod && this.appointmentData) {
      // Store payment method in session storage
      sessionStorage.setItem('selectedPaymentMethod', this.selectedMethod);
      
      // Navigate to payment form
      this.router.navigate(['/payments/form']);
    }
  }

  goBack(): void {
    this.router.navigate(['/appointments/schedule']);
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatTime(time: string): string {
    if (!time) return '';
    return new Date(`2000-01-01T${time}`).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }
}
