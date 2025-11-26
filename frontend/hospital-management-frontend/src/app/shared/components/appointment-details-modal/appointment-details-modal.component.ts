import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

interface AppointmentDetails {
  id: number;
  patient: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    contact?: string;
    gender?: string;
    bloodGroup?: string;
  };
  doctor: {
    id: number;
    firstName: string;
    lastName: string;
    specialization: string;
    consultationFee: number;
  };
  appointmentDate: string;
  appointmentTime: string;
  endTime: string;
  appointmentType: string;
  status: string;
  consultationFee: number;
  symptoms?: string;
  notes?: string;
  cancelledByName?: string;
  cancelledByType?: string;
  cancelledByUser?: number;
  cancelledByDoctor?: number;
  cancelledAt?: string;
  cancellationReason?: string;
  createdAt: string;
}

@Component({
  selector: 'app-appointment-details-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appointment-details-modal.component.html',
  styleUrls: ['./appointment-details-modal.component.css']
})
export class AppointmentDetailsModalComponent implements OnInit {
  @Input() appointmentId: number | null = null;
  @Input() showModal: boolean = false;
  @Input() userRole: string = 'PATIENT'; // PATIENT, DOCTOR, ADMIN
  @Output() closeModal = new EventEmitter<void>();
  @Output() appointmentCancelled = new EventEmitter<void>();

  appointment: AppointmentDetails | null = null;
  isLoading = false;
  error: string | null = null;
  showCancelModal = false;
  cancelReason = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (this.appointmentId && this.showModal) {
      this.loadAppointmentDetails();
    }
  }

  ngOnChanges(): void {
    if (this.appointmentId && this.showModal) {
      this.loadAppointmentDetails();
    }
  }

  loadAppointmentDetails(): void {
    if (!this.appointmentId) return;

    this.isLoading = true;
    this.error = null;

    const token = this.authService.getToken();
    this.http.get<{success: boolean, data: AppointmentDetails, message: string}>(`http://localhost:8080/api/appointments/${this.appointmentId}/details`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }).subscribe({
      next: (response) => {
        if (response.success) {
          this.appointment = response.data;
        } else {
          this.error = response.message;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading appointment details:', error);
        this.error = 'Failed to load appointment details';
        this.isLoading = false;
      }
    });
  }

  onCloseModal(): void {
    this.appointment = null;
    this.error = null;
    this.closeModal.emit();
  }

  onCancelAppointment(): void {
    this.showCancelModal = true;
  }

  onCloseCancelModal(): void {
    this.showCancelModal = false;
    this.cancelReason = '';
  }

  confirmCancel(): void {
    if (!this.appointmentId || !this.cancelReason.trim()) return;

    const token = this.authService.getToken();
    this.http.put(`http://localhost:8080/api/appointments/${this.appointmentId}/cancel`, null, {
      params: { reason: this.cancelReason },
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }).subscribe({
      next: () => {
        this.onCloseCancelModal();
        this.appointmentCancelled.emit();
        this.loadAppointmentDetails(); // Reload to show updated status
      },
      error: (error) => {
        console.error('Error cancelling appointment:', error);
        this.error = 'Failed to cancel appointment';
      }
    });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatTime(timeString: string): string {
    return new Date(`2000-01-01T${timeString}`).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'SCHEDULED':
        return 'badge--blue';
      case 'COMPLETED':
        return 'badge--green';
      case 'CANCELLED':
        return 'badge--red';
      default:
        return 'badge--gray';
    }
  }

  getTypeBadgeClass(type: string): string {
    switch (type) {
      case 'CONSULTATION':
        return 'badge--blue';
      case 'FOLLOW_UP':
        return 'badge--purple';
      case 'EMERGENCY':
        return 'badge--red';
      default:
        return 'badge--gray';
    }
  }

  canCancelAppointment(): boolean {
    return this.appointment?.status === 'SCHEDULED';
  }

  shouldShowSensitiveInfo(): boolean {
    return this.userRole === 'PATIENT' || this.userRole === 'DOCTOR';
  }

  isAdmin(): boolean {
    return this.userRole === 'ADMIN';
  }
}
