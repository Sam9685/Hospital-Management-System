// frontend/hospital-management-frontend/src/app/features/complaints/complaint-tracking/complaint-tracking.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { environment } from '../../../../environments/environment';

interface Complaint {
  complaintId: number;
  title: string;
  description: string;
  category: string;
  priority: string;
  status: string;
  contactPreference: string;
  createdAt: string;
  updatedAt: string;
  appointment?: {
    id: number;
    appointmentDate: string;
    appointmentTime: string;
    doctor: {
      doctorId: number;
      user: {
        firstname: string;
        lastname: string;
      };
      specialization: {
        name: string;
      };
    };
  };
  resolution?: string;
  resolutionNotes?: string;
  customerFeedback?: string;
  assignedTo?: {
    id: number;
    name: string;
    email: string;
  };
}

@Component({
  selector: 'app-complaint-tracking',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="complaint-tracking-container">
      <div class="tracking-header">
        <h1>Complaint Tracking</h1>
        <p>Track the status and progress of your submitted complaints</p>
      </div>

      <!-- Loading State -->
      <div *ngIf="isLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Loading your complaints...</p>
      </div>

      <!-- Complaints List -->
      <div *ngIf="!isLoading" class="complaints-list">
        <div class="complaints-header">
          <h2>Your Complaints ({{ totalElements }})</h2>
          <div class="filter-controls">
            <select [(ngModel)]="statusFilter" (change)="filterComplaints()" class="filter-select">
              <option value="">All Statuses</option>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
            <select [(ngModel)]="pageSize" (change)="onPageSizeChange(pageSize)" class="filter-select">
              <option *ngFor="let size of pageSizeOptions" [value]="size">{{ size }} per page</option>
            </select>
          </div>
        </div>

        <!-- No Complaints State -->
        <div *ngIf="complaints.length === 0" class="no-complaints-state">
          <div class="no-complaints-content">
            <svg class="no-complaints-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/>
              <circle cx="12" cy="13" r="3"/>
            </svg>
            <h3>No Complaints Found</h3>
            <p *ngIf="statusFilter">No complaints found for the selected filter. Try changing your filter criteria.</p>
            <p *ngIf="!statusFilter">You haven't submitted any complaints yet. <a routerLink="/complaints">Register a complaint</a> to get started.</p>
          </div>
        </div>

        <!-- Table Container -->
        <div *ngIf="complaints.length > 0" class="table-container">
          <table class="complaints-table">
            <thead>
              <tr>
                <th class="sortable" (click)="onSortChange('title')">
                  Title
                  <span class="sort-indicator" *ngIf="sortBy === 'title'">
                    {{ sortDir === 'asc' ? '↑' : '↓' }}
                  </span>
                </th>
                <th class="sortable" (click)="onSortChange('category')">
                  Category
                  <span class="sort-indicator" *ngIf="sortBy === 'category'">
                    {{ sortDir === 'asc' ? '↑' : '↓' }}
                  </span>
                </th>
                <th class="sortable" (click)="onSortChange('status')">
                  Status
                  <span class="sort-indicator" *ngIf="sortBy === 'status'">
                    {{ sortDir === 'asc' ? '↑' : '↓' }}
                  </span>
                </th>
                <th class="sortable" (click)="onSortChange('createdAt')">
                  Created
                  <span class="sort-indicator" *ngIf="sortBy === 'createdAt'">
                    {{ sortDir === 'asc' ? '↑' : '↓' }}
                  </span>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let complaint of complaints" class="complaint-row">
                <td class="complaint-title-cell">
                  <div class="complaint-title">{{ complaint.title }}</div>
                  <div class="complaint-description">{{ complaint.description.length > 100 ? complaint.description.substring(0, 100) + '...' : complaint.description }}</div>
                </td>
                <td class="complaint-category-cell">
                  <span class="complaint-category">{{ formatEnum(complaint.category) }}</span>
                </td>
                <td class="complaint-status-cell">
                  <span class="complaint-status" [class]="'status-' + complaint.status.toLowerCase().replace('_', '-')">
                    {{ formatEnum(complaint.status) }}
                  </span>
                </td>
                <td class="complaint-date-cell">
                  <div class="complaint-date">{{ formatDate(complaint.createdAt) }}</div>
                  <div *ngIf="complaint.updatedAt !== complaint.createdAt" class="complaint-updated">
                    Updated: {{ formatDate(complaint.updatedAt) }}
                  </div>
                </td>
                <td class="complaint-actions-cell">
                  <button class="btn-view-details" (click)="openComplaintDetail(complaint)" title="View Details">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination Controls -->
        <div *ngIf="complaints.length > 0" class="pagination-container">
          <div class="pagination-info">
            Showing {{ (currentPage * pageSize) + 1 }} to {{ Math.min((currentPage + 1) * pageSize, totalElements) }} of {{ totalElements }} complaints
          </div>
          <div class="pagination-controls">
            <button 
              class="pagination-btn" 
              [disabled]="currentPage === 0" 
              (click)="onPageChange(0)"
              title="First Page"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="11 17 6 12 11 7"/>
                <polyline points="18 17 13 12 18 7"/>
              </svg>
            </button>
            <button 
              class="pagination-btn" 
              [disabled]="currentPage === 0" 
              (click)="onPageChange(currentPage - 1)"
              title="Previous Page"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="15 18 9 12 15 6"/>
              </svg>
            </button>
            
            <div class="page-numbers">
              <button 
                *ngFor="let page of getPageNumbers()" 
                class="pagination-btn page-number"
                [class.active]="page === currentPage"
                (click)="onPageChange(page)"
              >
                {{ page + 1 }}
              </button>
            </div>
            
            <button 
              class="pagination-btn" 
              [disabled]="currentPage >= totalPages - 1" 
              (click)="onPageChange(currentPage + 1)"
              title="Next Page"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </button>
            <button 
              class="pagination-btn" 
              [disabled]="currentPage >= totalPages - 1" 
              (click)="onPageChange(totalPages - 1)"
              title="Last Page"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="13 17 18 12 13 7"/>
                <polyline points="6 17 11 12 6 7"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Complaint Detail Modal -->
      <div *ngIf="selectedComplaint" class="modal-overlay" (click)="closeComplaintDetail()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Complaint Details</h2>
            <button class="modal-close" (click)="closeComplaintDetail()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>

          <div class="modal-body">
            <!-- Complaint Header -->
            <div class="complaint-detail-header">
              <div class="complaint-detail-title">{{ selectedComplaint.title }}</div>
              <div class="complaint-detail-status" [class]="'status-' + selectedComplaint.status.toLowerCase().replace('_', '-')">
                {{ formatEnum(selectedComplaint.status) }}
              </div>
            </div>

            <!-- Complaint Meta -->
            <div class="complaint-detail-meta">
              <div class="meta-item">
                <label>Category:</label>
                <span class="complaint-category">{{ formatEnum(selectedComplaint.category) }}</span>
              </div>
              <div class="meta-item">
                <label>Priority:</label>
                <span class="complaint-priority priority-{{ selectedComplaint.priority.toLowerCase() }}">
                  {{ formatEnum(selectedComplaint.priority) }}
                </span>
              </div>
              <div class="meta-item">
                <label>Contact Preference:</label>
                <span>{{ formatEnum(selectedComplaint.contactPreference) }}</span>
              </div>
              <div class="meta-item">
                <label>Submitted:</label>
                <span>{{ formatDate(selectedComplaint.createdAt) }}</span>
              </div>
              <div class="meta-item" *ngIf="selectedComplaint.updatedAt !== selectedComplaint.createdAt">
                <label>Last Updated:</label>
                <span>{{ formatDate(selectedComplaint.updatedAt) }}</span>
              </div>
            </div>

            <!-- Complaint Description -->
            <div class="complaint-detail-section">
              <h3>Description</h3>
              <div class="complaint-description-full">{{ selectedComplaint.description }}</div>
            </div>

            <!-- Related Appointment -->
            <div *ngIf="selectedComplaint.appointment" class="complaint-detail-section">
              <h3>Related Appointment</h3>
              <div class="appointment-details">
                <div class="appointment-info">
                  <strong>Appointment #{{ selectedComplaint.appointment.id }}</strong>
                  <div class="appointment-meta">
                    <span>Date: {{ formatDate(selectedComplaint.appointment.appointmentDate) }}</span>
                    <span>Time: {{ selectedComplaint.appointment.appointmentTime }}</span>
                    <span>Doctor: {{ formatDoctorName(selectedComplaint.appointment.doctor) }}</span>
                    <span>Specialization: {{ selectedComplaint.appointment.doctor.specialization.name }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Assignment Info -->
            // <div *ngIf="selectedComplaint.assignedTo" class="complaint-detail-section">
            //   <h3>Assignment</h3>
            //   <div class="assignment-info">
            //     <strong>Assigned to:</strong> {{ selectedComplaint.assignedTo.name }}
            //     <div class="assignment-contact">Email: {{ selectedComplaint.assignedTo.email }}</div>
            //   </div>
            // </div>

            <!-- Resolution -->
            <div *ngIf="selectedComplaint.resolution" class="complaint-detail-section">
              <h3>Resolution</h3>
              <div class="resolution-content">
                <div class="resolution-text">{{ selectedComplaint.resolution }}</div>
                <div *ngIf="selectedComplaint.resolutionNotes" class="resolution-notes">
                  <strong>Resolution Notes:</strong> {{ selectedComplaint.resolutionNotes }}
                </div>
              </div>
            </div>

            <!-- Customer Feedback -->
            <div *ngIf="selectedComplaint.customerFeedback" class="complaint-detail-section">
              <h3>Your Feedback</h3>
              <div class="customer-feedback">{{ selectedComplaint.customerFeedback }}</div>
            </div>

          </div>

          <!-- Feedback Form -->
          <div *ngIf="showFeedbackForm" class="feedback-form">
            <div class="feedback-form-content">
              <h3>Provide Your Feedback</h3>
              <p class="feedback-description">Please share your thoughts about how this complaint was resolved.</p>
              <textarea 
                [(ngModel)]="feedbackText" 
                placeholder="Enter your feedback here..."
                class="feedback-textarea"
                rows="4"
                maxlength="1000">
              </textarea>
              <div class="feedback-actions">
                <button class="btn-secondary" (click)="cancelFeedback()" [disabled]="isSubmittingFeedback">
                  Cancel
                </button>
                <button class="btn-primary" (click)="submitFeedback()" [disabled]="isSubmittingFeedback || !feedbackText.trim()">
                  <span *ngIf="isSubmittingFeedback">Submitting...</span>
                  <span *ngIf="!isSubmittingFeedback">Submit Feedback</span>
                </button>
              </div>
            </div>
          </div>

          <!-- Modal Footer -->
          <div class="modal-footer" *ngIf="!showFeedbackForm">
            <button class="btn-secondary" (click)="closeComplaintDetail()">Close</button>
            <button *ngIf="selectedComplaint.status === 'RESOLVED' || selectedComplaint.status === 'CLOSED'" 
                    class="btn-primary" (click)="provideFeedback()">
              {{ selectedComplaint.customerFeedback ? 'Update Feedback' : 'Provide Feedback' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./complaint-tracking.component.css']
})
export class ComplaintTrackingComponent implements OnInit {
  complaints: Complaint[] = [];
  selectedComplaint: Complaint | null = null;
  isLoading = false;
  statusFilter = '';
  showFeedbackForm = false;
  feedbackText = '';
  isSubmittingFeedback = false;
  
  // Pagination properties
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  pageSizeOptions = [5, 10, 20, 50];
  sortBy = 'createdAt';
  sortDir = 'desc';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadComplaints();
  }

  loadComplaints(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.id) {
      this.toastService.showError('User not authenticated');
      return;
    }

    this.isLoading = true;
    const params = new URLSearchParams({
      page: this.currentPage.toString(),
      size: this.pageSize.toString(),
      sortBy: this.sortBy,
      sortDir: this.sortDir
    });
    
    if (this.statusFilter) {
      params.append('status', this.statusFilter);
    }

    this.http.get<any>(`${environment.apiUrl}/api/simple-complaints/patient/${currentUser.id}?${params}`).subscribe({
      next: (response) => {
        this.complaints = response.data.complaints;
        this.currentPage = response.data.currentPage;
        this.totalPages = response.data.totalPages;
        this.totalElements = response.data.totalElements;
        this.isLoading = false;
        console.log('Complaints loaded:', this.complaints);
        console.log('Pagination info:', {
          currentPage: this.currentPage,
          totalPages: this.totalPages,
          totalElements: this.totalElements
        });
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading complaints:', error);
        this.toastService.showError('Failed to load complaints');
      }
    });
  }

  filterComplaints(): void {
    this.currentPage = 0; // Reset to first page when filtering
    this.loadComplaints();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadComplaints();
  }

  onPageSizeChange(size: number): void {
    this.pageSize = size;
    this.currentPage = 0; // Reset to first page when changing page size
    this.loadComplaints();
  }

  onSortChange(sortBy: string): void {
    if (this.sortBy === sortBy) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = sortBy;
      this.sortDir = 'desc';
    }
    this.currentPage = 0; // Reset to first page when sorting
    this.loadComplaints();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const startPage = Math.max(0, this.currentPage - 2);
    const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  // Expose Math to template
  Math = Math;

  openComplaintDetail(complaint: Complaint): void {
    this.selectedComplaint = complaint;
  }

  closeComplaintDetail(): void {
    this.selectedComplaint = null;
  }

  provideFeedback(): void {
    this.showFeedbackForm = true;
    this.feedbackText = this.selectedComplaint?.customerFeedback || '';
  }

  submitFeedback(): void {
    if (!this.selectedComplaint || !this.feedbackText.trim()) {
      this.toastService.showError('Please enter your feedback');
      return;
    }

    this.isSubmittingFeedback = true;
    const feedbackData = {
      customerFeedback: this.feedbackText.trim()
    };

    this.http.put<any>(`${environment.apiUrl}/api/simple-complaints/${this.selectedComplaint.complaintId}/feedback`, feedbackData).subscribe({
      next: (response) => {
        this.isSubmittingFeedback = false;
        this.showFeedbackForm = false;
        this.feedbackText = '';
        
        // Update the complaint in the list
        const complaintIndex = this.complaints.findIndex(c => c.complaintId === this.selectedComplaint!.complaintId);
        if (complaintIndex !== -1) {
          this.complaints[complaintIndex].customerFeedback = feedbackData.customerFeedback;
          this.selectedComplaint!.customerFeedback = feedbackData.customerFeedback;
        }
        
        this.toastService.showSuccess('Feedback submitted successfully!');
        
        // Auto-close the modal after successful submission
        setTimeout(() => {
          this.closeComplaintDetail();
        }, 1500);
      },
      error: (error) => {
        this.isSubmittingFeedback = false;
        console.error('Error submitting feedback:', error);
        this.toastService.showError('Failed to submit feedback');
      }
    });
  }

  cancelFeedback(): void {
    this.showFeedbackForm = false;
    this.feedbackText = '';
  }

  formatEnum(value: string): string {
    if (!value) return '';
    return value.split('_').map(word => 
      word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
    ).join(' ');
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' at ' + date.toLocaleTimeString();
  }

  formatAppointmentInfo(appointment: any): string {
    const date = new Date(appointment.appointmentDate);
    const formattedDate = date.toLocaleDateString();
    const doctorName = appointment.doctor?.firstName && appointment.doctor?.lastName ? 
      `Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}` : 
      'Unknown Doctor';
    return `Appointment #${appointment.id} - ${formattedDate} at ${appointment.appointmentTime} (Dr. ${doctorName})`;
  }

  formatDoctorName(doctor: any): string {
    return doctor?.firstName && doctor?.lastName ? 
      `Dr. ${doctor.firstName} ${doctor.lastName}` : 
      'Unknown Doctor';
  }
}
