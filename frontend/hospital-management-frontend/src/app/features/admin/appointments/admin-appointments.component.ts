import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService, Appointment, SearchFilters, PaginatedResponse } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { AppointmentDetailsModalComponent } from '../../../shared/components/appointment-details-modal/appointment-details-modal.component';
import { CancelAppointmentModalComponent } from '../../../shared/components/cancel-appointment-modal/cancel-appointment-modal.component';

@Component({
  selector: 'app-admin-appointments',
  templateUrl: './admin-appointments.component.html',
  styleUrls: ['./admin-appointments.component.css'],
  imports: [CommonModule, ReactiveFormsModule, AppointmentDetailsModalComponent, CancelAppointmentModalComponent],
  standalone: true
})
export class AdminAppointmentsComponent implements OnInit {
  @ViewChild('editModal') editModal!: TemplateRef<any>;
  @ViewChild('deleteModal') deleteModal!: TemplateRef<any>;

  appointments: Appointment[] = [];
  paginatedResponse: PaginatedResponse<Appointment> | null = null;
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
  
  // Edit/Delete
  selectedAppointment: Appointment | null = null;
  showEditModal = false;
  showDeleteModal = false;
  editForm: FormGroup;
  isSubmitting = false;
  
  // Modal properties
  showAppointmentDetailsModal = false;
  selectedAppointmentId: number | null = null;
  showCancelModal = false;
  selectedAppointmentForCancel: Appointment | null = null;
  
  // Table columns
  columns = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'patient', label: 'Patient', sortable: true },
    { key: 'doctor', label: 'Doctor', sortable: true },
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
    // { value: 'EMERGENCY', label: 'Emergency' }
  ];

  constructor(
    private adminService: AdminService,
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

    this.editForm = this.fb.group({
      appointmentDate: ['', [Validators.required]],
      appointmentTime: ['', [Validators.required]],
      appointmentType: ['CONSULTATION', [Validators.required]],
      status: ['SCHEDULED', [Validators.required]],
      symptoms: [''],
      notes: [''],
      consultationFee: [0, [Validators.required, Validators.min(0)]]
    });

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
      console.error('User not authenticated');
      this.isLoading = false;
      return;
    }
    
    const token = this.authService.getToken();
    console.log('Current token for appointments:', token ? `${token.substring(0, 20)}...` : 'No token');
    
    const filters: SearchFilters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      patientName: this.searchTerm || undefined,
      doctorName: this.searchTerm || undefined,
      status: this.selectedStatus || undefined,
      appointmentType: this.selectedType || undefined,
      dateFrom: this.selectedDateFrom || undefined,
      dateTo: this.selectedDateTo || undefined
    };

    console.log('Loading appointments with filters:', filters);
    this.adminService.getAppointments(filters).subscribe({
      next: (response) => {
        console.log('Appointments response:', response);
        if (response.success) {
          this.paginatedResponse = response.data;
          this.appointments = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        } else {
          console.error('API returned error:', response.message);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading appointments:', error);
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

  editAppointment(appointment: Appointment): void {
    if (appointment.deletedAt) {
      return; // Don't allow editing deleted appointments
    }
    this.selectedAppointment = appointment;
    this.showEditModal = true;
    this.editForm.patchValue({
      appointmentDate: appointment.appointmentDate.split('T')[0],
      appointmentTime: appointment.appointmentTime,
      appointmentType: appointment.appointmentType,
      status: appointment.status,
      symptoms: appointment.symptoms || '',
      notes: appointment.notes || '',
      consultationFee: appointment.consultationFee
    });
  }

  updateAppointment(): void {
    if (this.editForm.valid && this.selectedAppointment) {
      this.isSubmitting = true;
      const updateData = this.editForm.value;
      
      this.adminService.updateAppointment(this.selectedAppointment.id, updateData).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadAppointments();
            this.closeEditModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error updating appointment:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  deleteAppointment(appointment: Appointment): void {
    if (appointment.deletedAt) {
      return; // Don't allow deleting already deleted appointments
    }
    this.selectedAppointment = appointment;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.selectedAppointment) {
      this.isSubmitting = true;
      this.adminService.deleteAppointment(this.selectedAppointment.id).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadAppointments();
            this.closeDeleteModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error deleting appointment:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  closeEditModal(): void {
    this.selectedAppointment = null;
    this.showEditModal = false;
    this.editForm.reset();
  }

  closeDeleteModal(): void {
    this.selectedAppointment = null;
    this.showDeleteModal = false;
  }

  onCloseAppointmentDetailsModal(): void {
    this.showAppointmentDetailsModal = false;
    this.selectedAppointmentId = null;
  }

  onAppointmentCancelled(): void {
    this.loadAppointments();
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

  formatDateTime(dateString: string, timeString: string): string {
    const date = new Date(dateString);
    const time = timeString.split(':').slice(0, 2).join(':');
    return `${date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} at ${time}`;
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
  
  cancelAppointment(appointment: Appointment) {
    this.selectedAppointmentForCancel = appointment;
    this.showCancelModal = true;
  }

  onCloseCancelModal() {
    this.showCancelModal = false;
    this.selectedAppointmentForCancel = null;
  }

  onConfirmCancel(event: { appointmentId: number, reason: string }) {
    this.adminService.cancelAppointment(event.appointmentId, event.reason).subscribe({
      next: (response) => {
        console.log('Appointment cancelled successfully:', response);
        this.loadAppointments();
        this.showCancelModal = false;
        this.selectedAppointmentForCancel = null;
      },
      error: (error) => {
        console.error('Error cancelling appointment:', error);
        alert('Failed to cancel appointment. Please try again.');
      }
    });
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