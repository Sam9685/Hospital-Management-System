import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="success-container">
      <div class="success-header">
        <div class="success-icon">
          <svg viewBox="0 0 24 24">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
        </div>
        <h1 class="success-title">Payment Successful!</h1>
        <p class="success-subtitle">Your appointment has been scheduled</p>
      </div>

      <div class="appointment-card">
        <div class="card-header">
          <h3>Appointment Scheduled</h3>
          <div class="status-badge scheduled">Scheduled</div>
        </div>
        
        <div class="appointment-details">
          <div class="detail-section">
            <h4>Doctor Information</h4>
            <div class="detail-item">
              <span class="label">Doctor:</span>
              <span class="value">{{ appointmentData?.doctorName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Specialization:</span>
              <span class="value">{{ appointmentData?.specialization }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>Appointment Details</h4>
            <div class="detail-item">
              <span class="label">Date:</span>
              <span class="value">{{ formatDate(appointmentData?.appointmentDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Time:</span>
              <span class="value">{{ formatTime(appointmentData?.appointmentTime) }} - {{ formatTime(appointmentData?.endTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Type:</span>
              <span class="value">{{ appointmentData?.appointmentType }}</span>
            </div>
            <div class="detail-item" *ngIf="appointmentData?.symptoms">
              <span class="label">Symptoms:</span>
              <span class="value">{{ appointmentData?.symptoms }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>Payment Information</h4>
            <div class="detail-item">
              <span class="label">Payment ID:</span>
              <span class="value">{{ paymentData?.paymentId }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Transaction ID:</span>
              <span class="value">{{ paymentData?.transactionId || getTransactionId() }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Amount Paid:</span>
              <span class="value amount">₹{{ appointmentData?.consultationFee }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Payment Method:</span>
              <span class="value">{{ paymentData?.method }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Payment Date:</span>
              <span class="value">{{ formatDateTime(paymentData?.paymentDate) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <button class="btn btn-secondary" (click)="downloadReceipt()">
          <svg class="btn-icon" viewBox="0 0 24 24">
            <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
          </svg>
          Download Receipt
        </button>
        <button class="btn btn-primary" (click)="goToAppointments()">
          <svg class="btn-icon" viewBox="0 0 24 24">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
          View My Appointments
        </button>
        <button class="btn btn-outline" (click)="goHome()">
          <svg class="btn-icon" viewBox="0 0 24 24">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
          </svg>
          Go Home
        </button>
      </div>

      <div class="next-steps">
        <h3>What's Next?</h3>
        <div class="steps-list">
          <div class="step-item">
            <div class="step-icon">
              <svg viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <div class="step-content">
              <h4>Appointment Scheduled</h4>
              <p>Your appointment has been successfully booked and scheduled.</p>
            </div>
          </div>
          
          <div class="step-item">
            <div class="step-icon">
              <svg viewBox="0 0 24 24">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="step-content">
              <h4>Reminder Notification</h4>
              <p>You will receive a reminder notification 24 hours before your appointment.</p>
            </div>
          </div>
          
          <div class="step-item">
            <div class="step-icon">
              <svg viewBox="0 0 24 24">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
            </div>
            <div class="step-content">
              <h4>Prepare for Visit</h4>
              <p>Bring your ID and any relevant medical documents to your appointment.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./payment-success.component.css']
})
export class PaymentSuccessComponent implements OnInit {
  appointmentData: any = null;
  paymentData: any = null;
  appointment: any = null;
  userProfile: any = null;

  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadSuccessData();
    this.loadUserProfile();
  }

  loadSuccessData(): void {
    const successData = sessionStorage.getItem('paymentSuccess');
    if (successData) {
      const data = JSON.parse(successData);
      this.appointmentData = data.appointmentData;
      this.paymentData = data.payment;
      this.appointment = data.appointment;
    } else {
      this.router.navigate(['/appointments/schedule']);
    }
  }

  loadUserProfile(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser?.id) {
      this.http.get<any>(`${environment.apiUrl}/api/simple-profile/user/${currentUser.id}`).subscribe({
        next: (response) => {
          this.userProfile = response.data;
        },
        error: (error) => {
          console.error('Error loading user profile:', error);
          // Fallback to basic user data
          this.userProfile = currentUser;
        }
      });
    }
  }

  downloadReceipt(): void {
    try {
      const pdf = new jsPDF();
      
      // Set up colors
      const primaryColor = '#3b82f6';
      const secondaryColor = '#6b7280';
      const successColor = '#059669';
      
      // Header
      pdf.setFillColor(primaryColor);
      pdf.rect(0, 0, 210, 30, 'F');
      
      pdf.setTextColor(255, 255, 255);
      pdf.setFontSize(20);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Hospital Management System', 105, 15, { align: 'center' });
      
      pdf.setFontSize(14);
      pdf.setFont('helvetica', 'normal');
      pdf.text('Appointment Receipt', 105, 22, { align: 'center' });
      
      // Reset text color
      pdf.setTextColor(0, 0, 0);
      
      // Receipt number and date
      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Receipt #: ${this.paymentData?.paymentId || 'N/A'}`, 15, 45);
      pdf.text(`Date: ${this.formatDateTime(new Date().toISOString())}`, 15, 50);
      
      // Patient Information
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Patient Information', 15, 65);
      
      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Name: ${this.userProfile?.name || this.appointmentData?.patientName || 'N/A'}`, 15, 75);
      pdf.text(`Email: ${this.userProfile?.email || this.appointmentData?.patientEmail || 'N/A'}`, 15, 80);
      pdf.text(`Phone: ${this.userProfile?.contact || this.appointmentData?.patientPhone || 'N/A'}`, 15, 85);
      if (this.userProfile?.address) {
        pdf.text(`Address: ${this.userProfile.address}`, 15, 90);
      }
      
      // Doctor Information
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Doctor Information', 15, 100);
      
      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Doctor: ${this.appointmentData?.doctorName || 'N/A'}`, 15, 110);
      pdf.text(`Specialization: ${this.appointmentData?.specialization || 'N/A'}`, 15, 115);
      pdf.text(`Experience: ${this.appointmentData?.yearsOfExp || 'N/A'} years`, 15, 120);
      
      // Appointment Details
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Appointment Details', 15, 135);
      
      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Date: ${this.formatDate(this.appointmentData?.appointmentDate)}`, 15, 145);
      pdf.text(`Time: ${this.formatTime(this.appointmentData?.appointmentTime)} - ${this.formatTime(this.appointmentData?.endTime)}`, 15, 150);
      pdf.text(`Type: ${this.appointmentData?.appointmentType || 'CONSULTATION'}`, 15, 155);
      
      if (this.appointmentData?.symptoms) {
        pdf.text(`Symptoms: ${this.appointmentData.symptoms}`, 15, 160);
      }
      
      // Payment Information
      pdf.setFontSize(12);
      pdf.setFont('helvetica', 'bold');
      pdf.text('Payment Information', 15, 175);
      
      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'normal');
      pdf.text(`Payment ID: ${this.paymentData?.paymentId || 'N/A'}`, 15, 185);
      pdf.text(`Transaction ID: ${this.paymentData?.transactionId || this.getTransactionId()}`, 15, 190);
      pdf.text(`Payment Method: ${this.paymentData?.method || 'Online Payment'}`, 15, 195);
      pdf.text(`Payment Date: ${this.formatDateTime(this.paymentData?.paymentDate || new Date().toISOString())}`, 15, 200);
      
      // Amount
      pdf.setFontSize(14);
      pdf.setFont('helvetica', 'bold');
      pdf.setTextColor(successColor);
      pdf.text(`Total Amount: Rs. ${this.appointmentData?.consultationFee || '0'}`, 15, 215);
      
      // Footer
      pdf.setTextColor(0, 0, 0);
      pdf.setFontSize(8);
      pdf.setFont('helvetica', 'normal');
      pdf.text('Thank you for choosing our hospital!', 105, 250, { align: 'center' });
      pdf.text('Please arrive 15 minutes before your appointment time.', 105, 255, { align: 'center' });
      pdf.text('For any queries, contact us at support@hospital.com', 105, 260, { align: 'center' });
      
      // Add border
      pdf.setDrawColor(200, 200, 200);
      pdf.rect(10, 35, 190, 220);
      
      // Download the PDF
      const fileName = `appointment-receipt-${this.paymentData?.paymentId || Date.now()}.pdf`;
      pdf.save(fileName);
      
      this.toastService.showSuccess('Receipt downloaded successfully!');
    } catch (error) {
      console.error('Error generating PDF:', error);
      this.toastService.showError('Error generating receipt. Please try again.');
    }
  }


  goToAppointments(): void {
    this.router.navigate(['/appointments/my-appointments']);
  }

  goHome(): void {
    this.router.navigate(['/home']);
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

  formatDateTime(dateTime: string): string {
    if (!dateTime) return '';
    return new Date(dateTime).toLocaleString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }

  getTransactionId(): string {
    return 'TXN' + Date.now();
  }
}
