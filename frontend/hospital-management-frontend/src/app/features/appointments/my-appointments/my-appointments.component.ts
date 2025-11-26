import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Appointment } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Router } from '@angular/router';
import { AppointmentDetailsModalComponent } from '../../../shared/components/appointment-details-modal/appointment-details-modal.component';

@Component({
  selector: 'app-my-appointments',
  standalone: true,
  imports: [CommonModule, FormsModule, AppointmentDetailsModalComponent],
  templateUrl: './my-appointments.component.html',
  styleUrls: ['./my-appointments.component.css']
})
export class MyAppointmentsComponent implements OnInit {
  activeTab: 'upcoming' | 'past' = 'upcoming';
  appointments: Appointment[] = [];
  appointmentStats = { totalUpcoming: 0, totalPast: 0 };
  
  // Pagination properties
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  pageSizeOptions = [5, 10, 20, 50];
  sortBy = 'appointmentDate';
  sortDir = 'desc';
  statusFilter = '';
  
  showRescheduleModal = false;
  showCancelModal = false;
  selectedAppointment: Appointment | null = null;
  rescheduleData = { appointmentDate: '', appointmentTime: '', endTime: '' };
  cancelReason = '';
  today = new Date().toISOString().split('T')[0];
  isLoading = false;
  hasFilteredResults = true;
  
  // Modal properties
  showAppointmentDetailsModal = false;
  selectedAppointmentId: number | null = null;
  
  // Reschedule modal properties
  availableSlots: any[] = [];
  selectedSlot: any = null;
  isLoadingSlots = false;

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.id) {
      this.toastService.showError('User not authenticated');
      return;
    }

    this.isLoading = true;
    
    // Determine status filter based on active tab
    let statusFilter = this.statusFilter;
    if (this.activeTab === 'upcoming') {
      // For upcoming, exclude cancelled appointments
      statusFilter = this.statusFilter === 'CANCELLED' ? '' : this.statusFilter;
    } else if (this.activeTab === 'past') {
      // For past, we can include all statuses including cancelled
      statusFilter = this.statusFilter;
    }

    this.adminService.getUserAppointmentsPaginated(currentUser.id, this.currentPage, this.pageSize, this.sortBy, this.sortDir, statusFilter, this.activeTab).subscribe({
      next: (data) => {
        this.appointments = data.appointments;
        this.currentPage = data.currentPage;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.hasFilteredResults = data.totalElements > 0;
        this.isLoading = false;
        console.log('Appointments loaded:', this.appointments);
      },
      error: (error) => {
        this.isLoading = false;
        this.hasFilteredResults = false;
        console.error('Error loading appointments:', error);
        this.toastService.showError('Failed to load appointments');
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadAppointments();
  }

  onPageSizeChange(size: number): void {
    this.pageSize = size;
    this.currentPage = 0;
    this.loadAppointments();
  }

  onSortChange(sortBy: string): void {
    if (this.sortBy === sortBy) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = sortBy;
      this.sortDir = 'desc';
    }
    this.currentPage = 0;
    this.loadAppointments();
  }

  onStatusFilterChange(): void {
    this.currentPage = 0;
    this.loadAppointments();
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

  setActiveTab(tab: 'upcoming' | 'past'): void {
    this.activeTab = tab;
    this.statusFilter = ''; // Reset status filter when switching tabs
    this.currentPage = 0; // Reset to first page
    this.loadAppointments();
  }

  viewAppointment(appointment: Appointment): void {
    this.selectedAppointmentId = appointment.id;
    this.showAppointmentDetailsModal = true;
  }

  rescheduleAppointment(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.rescheduleData = {
      appointmentDate: '',
      appointmentTime: '',
      endTime: ''
    };
    this.availableSlots = [];
    this.selectedSlot = null;
    this.showRescheduleModal = true;
  }

  closeRescheduleModal(): void {
    this.showRescheduleModal = false;
    this.selectedAppointment = null;
    this.rescheduleData = { appointmentDate: '', appointmentTime: '', endTime: '' };
    this.availableSlots = [];
    this.selectedSlot = null;
    this.isLoadingSlots = false;
  }

  onRescheduleDateChange(): void {
    if (this.rescheduleData.appointmentDate && this.selectedAppointment?.doctor?.doctorId) {
      this.loadAvailableSlots();
    }
  }

  loadAvailableSlots(): void {
    if (!this.selectedAppointment?.doctor?.doctorId || !this.rescheduleData.appointmentDate) {
      return;
    }

    this.isLoadingSlots = true;
    this.availableSlots = [];
    this.selectedSlot = null;

    console.log('Loading slots for doctor:', this.selectedAppointment.doctor.doctorId, 'date:', this.rescheduleData.appointmentDate);

    this.adminService.getDoctorSlots(
      this.selectedAppointment.doctor.doctorId, 
      this.rescheduleData.appointmentDate
    ).subscribe({
      next: (response) => {
        if (response.success) {
          this.availableSlots = response.data || [];
          console.log('Available slots loaded:', this.availableSlots);
          if (this.availableSlots.length > 0) {
            console.log('First slot example:', this.availableSlots[0]);
            console.log('Start time type:', typeof this.availableSlots[0].startTime);
            console.log('Start time value:', this.availableSlots[0].startTime);
          }
        }
        this.isLoadingSlots = false;
      },
      error: (error) => {
        console.error('Error loading available slots:', error);
        this.toastService.showError('Failed to load available slots');
        this.isLoadingSlots = false;
      }
    });
  }

  selectSlot(slot: any): void {
    console.log('=== SLOT SELECTION DEBUG ===');
    console.log('Selected slot:', slot);
    console.log('Slot start time:', slot.startTime, 'type:', typeof slot.startTime);
    console.log('Slot end time:', slot.endTime, 'type:', typeof slot.endTime);
    
    this.selectedSlot = slot;
    this.rescheduleData.appointmentTime = slot.startTime;
    this.rescheduleData.endTime = slot.endTime;
    
    console.log('Updated reschedule data:', this.rescheduleData);
  }

  confirmReschedule(): void {
    if (!this.selectedAppointment || !this.selectedSlot) return;

    console.log('=== RESCHEDULE DEBUG ===');
    console.log('Selected slot:', this.selectedSlot);
    console.log('Reschedule data:', this.rescheduleData);
    console.log('Appointment time string:', this.rescheduleData.appointmentTime);
    console.log('End time string:', this.rescheduleData.endTime);

    // Parse time with better error handling
    let appointmentTime, endTime;
    
    try {
      const startTimeParts = this.rescheduleData.appointmentTime.split(':');
      appointmentTime = {
        hour: parseInt(startTimeParts[0]),
        minute: parseInt(startTimeParts[1]),
        second: 0,
        nano: 0
      };
    } catch (error) {
      console.error('Error parsing appointment time:', error);
      this.toastService.showError('Invalid appointment time format');
      return;
    }

    try {
      const endTimeParts = this.rescheduleData.endTime.split(':');
      endTime = {
        hour: parseInt(endTimeParts[0]),
        minute: parseInt(endTimeParts[1]),
        second: 0,
        nano: 0
      };
    } catch (error) {
      console.error('Error parsing end time:', error);
      this.toastService.showError('Invalid end time format');
      return;
    }

    const appointmentData = {
      appointmentDate: this.rescheduleData.appointmentDate,
      appointmentTime: appointmentTime,
      endTime: endTime
    };

    console.log('Final appointment data:', appointmentData);

    this.adminService.rescheduleAppointment(this.selectedAppointment.id!, appointmentData).subscribe({
      next: () => {
        this.toastService.showSuccess('Appointment rescheduled successfully');
        this.closeRescheduleModal();
        this.loadAppointments();
      },
      error: (error) => {
        console.error('Reschedule error:', error);
        this.toastService.showError(error.error?.message || 'Failed to reschedule appointment');
      }
    });
  }

  cancelAppointment(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.cancelReason = '';
    this.showCancelModal = true;
  }

  closeCancelModal(): void {
    this.showCancelModal = false;
    this.selectedAppointment = null;
    this.cancelReason = '';
  }

  onCloseAppointmentDetailsModal(): void {
    this.showAppointmentDetailsModal = false;
    this.selectedAppointmentId = null;
  }

  onAppointmentCancelled(): void {
    this.loadAppointments();
  }

  confirmCancel(): void {
    if (!this.selectedAppointment) return;

    this.adminService.cancelAppointment(this.selectedAppointment.id!, this.cancelReason).subscribe({
      next: () => {
        this.toastService.showSuccess('Appointment cancelled successfully');
        this.closeCancelModal();
        this.loadAppointments();
      },
      error: (error) => {
        this.toastService.showError(error.error?.message || 'Failed to cancel appointment');
      }
    });
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatTime(time: string): string {
    return new Date(`2000-01-01T${time}`).toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  }

  getDayOfWeek(date: string): string {
    return new Date(date).toLocaleDateString('en-US', { weekday: 'short' });
  }

  isAppointmentUpcoming(appointment: Appointment): boolean {
    const appointmentDate = new Date(appointment.appointmentDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return appointmentDate >= today && appointment.status !== 'CANCELLED';
  }

  shouldShowActions(appointment: Appointment): boolean {
    return this.activeTab === 'upcoming' && this.isAppointmentUpcoming(appointment);
  }

  getDoctorName(doctor: any): string {
    return `Dr. ${doctor.firstName} ${doctor.lastName}`;
  }

  bookNewAppointment(): void {
    this.router.navigate(['/appointments/schedule']);
  }
}