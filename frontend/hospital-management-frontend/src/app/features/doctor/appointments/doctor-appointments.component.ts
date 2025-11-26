import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { AppointmentDetailsModalComponent } from '../../../shared/components/appointment-details-modal/appointment-details-modal.component';

interface Appointment {
  id: number;
  patient: {
    id: number;
    name: string;
    email: string;
    contact?: string;
  };
  doctor: {
    id: number;
    firstName: string;
    lastName: string;
    specialization: {
      name: string;
    };
  };
  appointmentDate: string;
  appointmentTime: string;
  appointmentType: string;
  status: string;
  consultationFee: number;
  symptoms?: string;
  notes?: string;
  deletedAt?: string;
}

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Component({
  selector: 'app-doctor-appointments',
  templateUrl: './doctor-appointments.component.html',
  styleUrls: ['./doctor-appointments.component.css'],
  imports: [CommonModule, ReactiveFormsModule, AppointmentDetailsModalComponent],
  standalone: true
})
export class DoctorAppointmentsComponent implements OnInit {
  appointments: Appointment[] = [];
  isLoading = false;
  searchTerm = '';
  searchSubject = new Subject<string>();
  
  // Make Math available in template
  Math = Math;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  
  // Sorting
  sortBy = 'appointmentDate';
  sortDir: 'asc' | 'desc' = 'desc';
  
  // Filters
  showFilters = false;
  filterForm: FormGroup;
  selectedStatus = '';
  selectedType = '';
  selectedDateFrom = '';
  selectedDateTo = '';
  
  // Date validation properties
  minToDate = '';
  maxFromDate = '';
  
  // Modal properties
  showAppointmentDetailsModal = false;
  selectedAppointmentId: number | null = null;
  
  // Table columns (removed doctor column since this is for doctor view)
  columns = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'patient', label: 'Patient', sortable: true },
    { key: 'appointmentDate', label: 'Date', sortable: true },
    { key: 'appointmentTime', label: 'Time', sortable: true },
    { key: 'appointmentType', label: 'Type', sortable: true },
    { key: 'status', label: 'Status', sortable: true },
    { key: 'consultationFee', label: 'Fee', sortable: true },
    { key: 'actions', label: 'Actions', sortable: false }
  ];

  statuses = [
    { value: '', label: 'All Status' },
    { value: 'SCHEDULED', label: 'Scheduled' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'CANCELLED', label: 'Cancelled' }
  ];

  types = [
    { value: '', label: 'All Types' },
    { value: 'CONSULTATION', label: 'Consultation' },
    { value: 'FOLLOW_UP', label: 'Follow Up' },
    { value: 'EMERGENCY', label: 'Emergency' }
  ];

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      status: [''],
      type: [''],
      dateFrom: [''],
      dateTo: ['']
    });

    // Setup date validation
    this.setupDateValidation();

    // Setup search debounce
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.searchTerm = searchTerm;
      this.currentPage = 0;
      this.loadAppointments();
    });
  }

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.isLoading = true;
    
    // Check authentication before making API call
    if (!this.authService.isLoggedIn()) {
      console.error('Doctor not authenticated');
      this.isLoading = false;
      return;
    }
    
    const token = this.authService.getToken();
    console.log('Current token for doctor appointments:', token ? `${token.substring(0, 20)}...` : 'No token');
    
    // Build query parameters
    const params = new URLSearchParams();
    params.set('page', this.currentPage.toString());
    params.set('size', this.pageSize.toString());
    params.set('sortBy', this.sortBy);
    params.set('sortDir', this.sortDir);
    
    if (this.searchTerm) {
      params.set('search', this.searchTerm);
    }
    if (this.selectedStatus) {
      params.set('status', this.selectedStatus);
    }
    if (this.selectedType) {
      params.set('appointmentType', this.selectedType);
    }
    if (this.selectedDateFrom) {
      params.set('dateFrom', this.selectedDateFrom);
    }
    if (this.selectedDateTo) {
      params.set('dateTo', this.selectedDateTo);
    }

    const url = `http://localhost:8080/api/appointments/doctor/my-appointments?${params.toString()}`;
    console.log('Loading doctor appointments from:', url);
    
    this.http.get<{success: boolean, data: PaginatedResponse<Appointment>, message: string}>(url, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }).subscribe({
      next: (response) => {
        console.log('Doctor appointments response:', response);
        if (response.success) {
          this.appointments = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        } else {
          console.error('API returned error:', response.message);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading doctor appointments:', error);
        console.error('Error details:', error.error);
        this.isLoading = false;
      }
    });
  }

  onSearch(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchSubject.next(target.value);
  }

  onSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDir = 'asc';
    }
    this.currentPage = 0;
    this.loadAppointments();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadAppointments();
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = +target.value;
    this.pageSize = size;
    this.currentPage = 0;
    this.loadAppointments();
  }

  applyFilters(): void {
    this.selectedStatus = this.filterForm.get('status')?.value || '';
    this.selectedType = this.filterForm.get('type')?.value || '';
    this.selectedDateFrom = this.filterForm.get('dateFrom')?.value || '';
    this.selectedDateTo = this.filterForm.get('dateTo')?.value || '';
    this.currentPage = 0;
    this.loadAppointments();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedStatus = '';
    this.selectedType = '';
    this.selectedDateFrom = '';
    this.selectedDateTo = '';
    this.currentPage = 0;
    this.loadAppointments();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  viewAppointment(appointment: Appointment): void {
    this.selectedAppointmentId = appointment.id;
    this.showAppointmentDetailsModal = true;
  }


  cancelAppointment(appointment: Appointment): void {
    this.selectedAppointmentId = appointment.id;
    this.showAppointmentDetailsModal = true;
  }

  onCloseAppointmentDetailsModal(): void {
    this.showAppointmentDetailsModal = false;
    this.selectedAppointmentId = null;
  }

  onAppointmentCancelled(): void {
    this.loadAppointments();
  }

  completeAppointment(appointment: Appointment): void {
    if (!this.canCompleteAppointment(appointment)) {
      return;
    }

    this.isLoading = true;
    this.http.put(`http://localhost:8080/api/appointments/${appointment.id}/complete`, {})
      .subscribe({
        next: (response: any) => {
          this.isLoading = false;
          if (response.success) {
            this.loadAppointments();
            // Show success message
            this.showToast('Appointment completed successfully', 'success');
          } else {
            this.showToast(response.message || 'Failed to complete appointment', 'error');
          }
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error completing appointment:', error);
          const errorMessage = error.error?.message || 'Failed to complete appointment';
          this.showToast(errorMessage, 'error');
        }
      });
  }

  canCompleteAppointment(appointment: Appointment): boolean {
    // Check if appointment is not already completed or cancelled
    if (appointment.status === 'COMPLETED' || appointment.status === 'CANCELLED') {
      return false;
    }

    // Check if appointment time has passed
    const appointmentDate = new Date(appointment.appointmentDate);
    const appointmentTime = appointment.appointmentTime;
    const [hours, minutes] = appointmentTime.split(':').map(Number);
    appointmentDate.setHours(hours, minutes, 0, 0);
    
    const now = new Date();
    return appointmentDate <= now;
  }

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    // Create a simple toast notification
    const toast = document.createElement('div');
    toast.className = `toast toast--${type}`;
    toast.textContent = message;
    toast.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      padding: 12px 20px;
      border-radius: 4px;
      color: white;
      font-weight: 500;
      z-index: 10000;
      max-width: 300px;
      word-wrap: break-word;
      ${type === 'success' ? 'background-color: #10b981;' : ''}
      ${type === 'error' ? 'background-color: #ef4444;' : ''}
      ${type === 'info' ? 'background-color: #3b82f6;' : ''}
    `;
    
    document.body.appendChild(toast);
    
    // Remove toast after 3 seconds
    setTimeout(() => {
      if (document.body.contains(toast)) {
        document.body.removeChild(toast);
      }
    }, 3000);
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

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getSortIcon(column: string): string {
    if (this.sortBy !== column) return 'sort';
    return this.sortDir === 'asc' ? 'sort-up' : 'sort-down';
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const start = Math.max(0, this.currentPage - 2);
    const end = Math.min(this.totalPages - 1, this.currentPage + 2);
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  // Date validation methods
  setupDateValidation(): void {
    // Listen to dateFrom changes
    this.filterForm.get('dateFrom')?.valueChanges.subscribe(dateFrom => {
      if (dateFrom) {
        this.minToDate = dateFrom;
        // If toDate is before fromDate, clear it
        const toDate = this.filterForm.get('dateTo')?.value;
        if (toDate && toDate < dateFrom) {
          this.filterForm.get('dateTo')?.setValue('');
        }
      } else {
        this.minToDate = '';
      }
    });

    // Listen to dateTo changes
    this.filterForm.get('dateTo')?.valueChanges.subscribe(dateTo => {
      if (dateTo) {
        this.maxFromDate = dateTo;
        // If fromDate is after toDate, clear it
        const fromDate = this.filterForm.get('dateFrom')?.value;
        if (fromDate && fromDate > dateTo) {
          this.filterForm.get('dateFrom')?.setValue('');
        }
      } else {
        this.maxFromDate = '';
      }
    });
  }

  onFromDateChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    const fromDate = target.value;
    
    if (fromDate) {
      this.minToDate = fromDate;
      // Clear toDate if it's before fromDate
      const toDate = this.filterForm.get('dateTo')?.value;
      if (toDate && toDate < fromDate) {
        this.filterForm.get('dateTo')?.setValue('');
      }
    } else {
      this.minToDate = '';
    }
  }

  onToDateChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    const toDate = target.value;
    
    if (toDate) {
      this.maxFromDate = toDate;
      // Clear fromDate if it's after toDate
      const fromDate = this.filterForm.get('dateFrom')?.value;
      if (fromDate && fromDate > toDate) {
        this.filterForm.get('dateFrom')?.setValue('');
      }
    } else {
      this.maxFromDate = '';
    }
  }
}
