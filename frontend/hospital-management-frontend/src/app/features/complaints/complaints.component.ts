// frontend/hospital-management-frontend/src/app/features/complaints/complaints.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { environment } from '../../../environments/environment';

interface Appointment {
  id: number;
  appointmentDate: string;
  appointmentTime: string;
  doctor: {
    doctorId: number;
    firstName: string;
    lastName: string;
    specialization: {
      name: string;
    };
  };
  status: string;
}

interface ComplaintCategories {
  categories: string[];
  contactPreferences: string[];
  priorities: string[];
  statuses: string[];
}

@Component({
  selector: 'app-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="complaints-container">
      <div class="complaints-header">
        <h1>Complaints Management</h1>
        <p>Register a complaint or view your complaint history</p>
        <div class="header-actions">
          <a routerLink="/complaints/tracking" class="btn-tracking">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 3h18v18H3zM9 9h6v6H9z"/>
              <path d="M9 1v6M15 1v6M9 17v6M15 17v6"/>
            </svg>
            Track Complaints
          </a>
        </div>
      </div>

      <div class="complaints-content">
        <!-- Complaint Registration Form -->
        <div class="complaint-form-section">
          <div class="section-header">
            <h2>
              <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/>
                <circle cx="12" cy="13" r="3"/>
              </svg>
              Register New Complaint
            </h2>
            <p>Help us improve our services by sharing your feedback</p>
          </div>

          <form [formGroup]="complaintForm" (ngSubmit)="onSubmit()" class="complaint-form">
            <!-- Basic Information -->
            <div class="form-section">
              <h3>Basic Information</h3>
              
              <div class="form-row">
                <div class="form-group">
                  <label for="category">Complaint Category *</label>
                  <select 
                    id="category" 
                    formControlName="category" 
                    class="form-control"
                    [class.error]="complaintForm.get('category')?.invalid && complaintForm.get('category')?.touched"
                  >
                    <option value="">Select Category</option>
                    <option *ngFor="let category of complaintCategories?.categories" [value]="category">
                      {{ formatEnum(category) }}
                    </option>
                  </select>
                  <div *ngIf="complaintForm.get('category')?.invalid && complaintForm.get('category')?.touched" class="error-message">
                    Please select a complaint category
                  </div>
                </div>

                <div class="form-group">
                  <label for="appointmentId">Related Appointment (Optional)</label>
                  <select 
                    id="appointmentId" 
                    formControlName="appointmentId" 
                    class="form-control"
                  >
                    <option value="">No specific appointment</option>
                    <option *ngFor="let appointment of pastAppointments" [value]="appointment.id">
                      {{ formatAppointmentOption(appointment) }}
                    </option>
                  </select>
                  <small class="form-hint">Select an appointment if this complaint is related to a specific visit</small>
                </div>
              </div>

              <div class="form-group">
                <label for="title">Complaint Title *</label>
                <input 
                  type="text" 
                  id="title" 
                  formControlName="title" 
                  class="form-control"
                  placeholder="Brief description of your complaint"
                  [class.error]="complaintForm.get('title')?.invalid && complaintForm.get('title')?.touched"
                >
                <div *ngIf="complaintForm.get('title')?.invalid && complaintForm.get('title')?.touched" class="error-message">
                  <span *ngIf="complaintForm.get('title')?.errors?.['required']">Title is required</span>
                  <span *ngIf="complaintForm.get('title')?.errors?.['minlength']">Title must be at least 10 characters</span>
                  <span *ngIf="complaintForm.get('title')?.errors?.['maxlength']">Title must not exceed 100 characters</span>
                </div>
                <small class="form-hint">{{ complaintForm.get('title')?.value?.length || 0 }}/100 characters</small>
              </div>
            </div>

            <!-- Detailed Description -->
            <div class="form-section">
              <h3>Detailed Description</h3>
              
              <div class="form-group">
                <label for="description">Description *</label>
                <textarea 
                  id="description" 
                  formControlName="description" 
                  class="form-control textarea"
                  rows="5"
                  placeholder="Please provide detailed information about your complaint. Include dates, times, and any relevant details that will help us understand and resolve your issue."
                  [class.error]="complaintForm.get('description')?.invalid && complaintForm.get('description')?.touched"
                ></textarea>
                <div *ngIf="complaintForm.get('description')?.invalid && complaintForm.get('description')?.touched" class="error-message">
                  <span *ngIf="complaintForm.get('description')?.errors?.['required']">Description is required</span>
                  <span *ngIf="complaintForm.get('description')?.errors?.['minlength']">Description must be at least 20 characters</span>
                  <span *ngIf="complaintForm.get('description')?.errors?.['maxlength']">Description must not exceed 500 characters</span>
                </div>
                <small class="form-hint">{{ complaintForm.get('description')?.value?.length || 0 }}/500 characters</small>
              </div>
            </div>

            <!-- Contact Preferences -->
            <div class="form-section">
              <h3>Contact Preferences</h3>
              
              <div class="form-group">
                <label for="contactPreference">Preferred Contact Method *</label>
                <select 
                  id="contactPreference" 
                  formControlName="contactPreference" 
                  class="form-control"
                  [class.error]="complaintForm.get('contactPreference')?.invalid && complaintForm.get('contactPreference')?.touched"
                >
                  <option value="">Select Contact Method</option>
                  <option *ngFor="let preference of complaintCategories?.contactPreferences" [value]="preference">
                    {{ formatEnum(preference) }}
                  </option>
                </select>
                <div *ngIf="complaintForm.get('contactPreference')?.invalid && complaintForm.get('contactPreference')?.touched" class="error-message">
                  Please select your preferred contact method
                </div>
                <small class="form-hint">How would you like us to contact you regarding this complaint?</small>
              </div>
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
              <button 
                type="button" 
                class="btn-secondary" 
                (click)="resetForm()"
                [disabled]="isLoading"
              >
                Reset Form
              </button>
              <button 
                type="submit" 
                class="btn-primary" 
                [disabled]="complaintForm.invalid || isLoading"
              >
                <span *ngIf="isLoading" class="loading-spinner"></span>
                {{ isLoading ? 'Submitting...' : 'Submit Complaint' }}
          </button>
            </div>
          </form>
        </div>

        <!-- Complaint History -->
        <div class="complaint-history-section" *ngIf="userComplaints.length > 0">
          <div class="section-header">
            <h2>
              <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 3h18v18H3zM9 9h6v6H9z"/>
                <path d="M9 1v6M15 1v6M9 17v6M15 17v6"/>
              </svg>
              Your Complaint History
            </h2>
            <p>Track the status of your submitted complaints</p>
          </div>

          <div class="complaints-list">
            <div *ngFor="let complaint of userComplaints" class="complaint-card">
              <div class="complaint-header">
                <div class="complaint-title">{{ complaint.title }}</div>
                <div class="complaint-status" [class]="'status-' + complaint.status.toLowerCase().replace('_', '-')">
                  {{ formatEnum(complaint.status) }}
                </div>
              </div>
              
              <div class="complaint-details">
                <div class="complaint-meta">
                  <span class="complaint-category">{{ formatEnum(complaint.category) }}</span>
                  <span class="complaint-priority priority-{{ complaint.priority.toLowerCase() }}">
                    {{ formatEnum(complaint.priority) }}
                  </span>
                  <span class="complaint-date">{{ formatDate(complaint.createdAt) }}</span>
                </div>
                
                <div class="complaint-description">{{ complaint.description }}</div>
                
                <div *ngIf="complaint.resolution" class="complaint-resolution">
                  <strong>Resolution:</strong> {{ complaint.resolution }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- No Complaints Message -->
        <div class="no-complaints" *ngIf="userComplaints.length === 0 && !isLoadingComplaints">
          <div class="no-complaints-content">
            <svg class="no-complaints-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/>
              <circle cx="12" cy="13" r="3"/>
            </svg>
            <h3>No Complaints Yet</h3>
            <p>You haven't submitted any complaints yet. Use the form above to register your first complaint.</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./complaints.component.css']
})
export class ComplaintsComponent implements OnInit {
  complaintForm: FormGroup;
  isLoading = false;
  isLoadingComplaints = false;
  pastAppointments: Appointment[] = [];
  userComplaints: any[] = [];
  complaintCategories: ComplaintCategories | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private authService: AuthService,
    private toastService: ToastService
  ) {
    this.complaintForm = this.fb.group({
      category: ['', Validators.required],
      appointmentId: [''],
      title: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(500)]],
      contactPreference: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadComplaintCategories();
    this.loadPastAppointments();
    this.loadUserComplaints();
  }

  loadComplaintCategories(): void {
    this.http.get<any>(`${environment.apiUrl}/api/simple-complaints/categories`).subscribe({
      next: (response) => {
        this.complaintCategories = response.data;
        console.log('Complaint categories loaded:', this.complaintCategories);
      },
      error: (error) => {
        console.error('Error loading complaint categories:', error);
        this.toastService.showError('Failed to load complaint categories');
      }
    });
  }

  loadPastAppointments(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.id) {
      console.log('No user ID available for loading appointments');
      return;
    }

    // Using the paginated past appointments endpoint that filters by date and time
    this.http.get<any>(`${environment.apiUrl}/api/appointments/patient/${currentUser.id}/paginated?page=0&size=100&sortBy=appointmentDate&sortDir=desc&type=past`).subscribe({
      next: (response) => {
        this.pastAppointments = response.appointments || [];
        console.log('Past appointments loaded for complaints dropdown:', this.pastAppointments);
      },
      error: (error) => {
        console.error('Error loading past appointments:', error);
        this.toastService.showError('Failed to load past appointments');
      }
    });
  }

  loadUserComplaints(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.id) {
      console.log('No user ID available for loading complaints');
      return;
    }

    this.isLoadingComplaints = true;
    this.http.get<any>(`${environment.apiUrl}/api/simple-complaints/patient/${currentUser.id}`).subscribe({
      next: (response) => {
        this.userComplaints = response.data;
        this.isLoadingComplaints = false;
        console.log('User complaints loaded:', this.userComplaints);
      },
      error: (error) => {
        this.isLoadingComplaints = false;
        console.error('Error loading user complaints:', error);
        this.toastService.showError('Failed to load complaint history');
      }
    });
  }

  onSubmit(): void {
    if (this.complaintForm.valid) {
      this.isLoading = true;
      
      const currentUser = this.authService.getCurrentUser();
      if (!currentUser?.id) {
        this.toastService.showError('User not authenticated');
        this.isLoading = false;
        return;
      }

      const complaintData = {
        patientId: currentUser.id,
        appointmentId: this.complaintForm.value.appointmentId || null,
        category: this.complaintForm.value.category,
        title: this.complaintForm.value.title,
        description: this.complaintForm.value.description,
        contactPreference: this.complaintForm.value.contactPreference,
        priority: 'MEDIUM' // Default priority
      };

      console.log('Submitting complaint:', complaintData);

      this.http.post<any>(`${environment.apiUrl}/api/simple-complaints`, complaintData).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log('Complaint submitted successfully:', response);
          this.toastService.showSuccess('Complaint submitted successfully!');
          this.resetForm();
          this.loadUserComplaints(); // Reload complaints to show the new one
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error submitting complaint:', error);
          this.toastService.showError('Failed to submit complaint: ' + error.message);
        }
      });
    } else {
      this.markFormGroupTouched();
      this.toastService.showError('Please fill in all required fields correctly');
    }
  }

  resetForm(): void {
    this.complaintForm.reset();
    this.complaintForm.patchValue({
      category: '',
      appointmentId: '',
      title: '',
      description: '',
      contactPreference: ''
    });
  }

  markFormGroupTouched(): void {
    Object.keys(this.complaintForm.controls).forEach(key => {
      const control = this.complaintForm.get(key);
      control?.markAsTouched();
    });
  }

  formatEnum(value: string): string {
    if (!value) return '';
    return value.split('_').map(word => 
      word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
    ).join(' ');
  }

  formatAppointmentOption(appointment: Appointment): string {
    const date = new Date(appointment.appointmentDate);
    const formattedDate = date.toLocaleDateString();
    const doctorName = appointment.doctor?.firstName && appointment.doctor?.lastName ? 
      `Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}` : 
      'Unknown Doctor';
    const specialization = appointment.doctor?.specialization?.name || 'General';
    const status = appointment.status || 'UNKNOWN';
    const statusText = status === 'SCHEDULED' ? '[UPCOMING]' : 
                      status === 'COMPLETED' ? '[COMPLETED]' : 
                      status === 'CANCELLED' ? '[CANCELLED]' : 
                      `[${status}]`;
    return `${statusText} Appointment #${appointment.id} - ${formattedDate} at ${appointment.appointmentTime} (Dr. ${doctorName} - ${specialization})`;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' at ' + date.toLocaleTimeString();
  }
}
