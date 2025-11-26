import { Component, OnInit, ChangeDetectorRef, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminService, Complaint, User, ApiResponse } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-complaint-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './complaint-details.component.html',
  styleUrls: ['./complaint-details.component.css']
})
export class ComplaintDetailsComponent implements OnInit, AfterViewInit {
  @ViewChild('statusSelect', { static: false }) statusSelect!: ElementRef;
  
  complaint: Complaint | null = null;
  admins: User[] = [];
  currentAdmin: any = null;
  isLoading = false;
  isSubmitting = false;
  canEdit = false;
  
  // Single admin form for all updates
  adminForm: FormGroup;
  
  // Simple status tracking
  selectedStatus: string = '';
  selectedPriority: string = '';
  
  // Status options - simple array
  statusOptions = [
    { value: 'OPEN', label: 'Open' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'RESOLVED', label: 'Resolved' },
    { value: 'CLOSED', label: 'Closed' }
  ];
  
  priorityOptions = [
    { value: 'LOW', label: 'Low' },
    { value: 'MEDIUM', label: 'Medium' },
    { value: 'HIGH', label: 'High' },
    { value: 'URGENT', label: 'Urgent' }
  ];

  // Get available status options based on current status
  getAvailableStatusOptions() {
    if (!this.complaint) return this.statusOptions;

    switch (this.complaint.status) {
      case 'OPEN':
        return this.statusOptions; // Can go to any status
      case 'IN_PROGRESS':
        return this.statusOptions.filter(option => option.value !== 'OPEN');
      case 'RESOLVED':
        return this.statusOptions.filter(option => !['OPEN', 'IN_PROGRESS'].includes(option.value));
      case 'CLOSED':
        return this.statusOptions.filter(option => option.value === 'CLOSED');
      default:
        return this.statusOptions;
    }
  }

  // Check if status is selected
  isStatusSelected(statusValue: string): boolean {
    return this.selectedStatus === statusValue;
  }

  // Handle status selection
  selectStatus(statusValue: string): void {
    console.log('Selecting status:', statusValue);
    this.selectedStatus = statusValue;
    this.adminForm.get('status')?.setValue(statusValue);
    this.cdr.detectChanges();
  }

  // Handle priority change
  onPriorityChange(event: any): void {
    const value = event.target.value;
    this.selectedPriority = value;
    this.adminForm.get('priority')?.setValue(value);
    this.cdr.detectChanges();
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adminService: AdminService,
    private authService: AuthService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.adminForm = this.fb.group({
      status: ['', [Validators.required]],
      priority: ['', [Validators.required]],
      assignedTo: [''],
      resolution: ['', [Validators.required]]
    });
    
    // Subscribe to form changes for debugging
    this.adminForm.valueChanges.subscribe(value => {
      console.log('Form value changed:', value);
    });
  }

  ngOnInit(): void {
    const complaintId = this.route.snapshot.paramMap.get('id');
    if (complaintId) {
      this.currentAdmin = this.authService.getCurrentUser();
      this.loadComplaintDetails(complaintId);
      this.loadAdmins();
    }
  }

  ngAfterViewInit(): void {
    // Ensure form is updated after view is initialized
    if (this.complaint) {
      setTimeout(() => {
        this.updateFormValues();
      }, 200);
    }
  }

  // Refresh form
  refreshForm(): void {
    if (this.complaint) {
      console.log('Refreshing form...');
      this.updateFormValues();
    }
  }

  loadComplaintDetails(id: string): void {
    this.isLoading = true;
    this.adminService.getComplaintById(parseInt(id)).subscribe({
      next: (response) => {
        if (response.success) {
          this.complaint = response.data;
          this.checkEditPermissions();
          console.log('Complaint loaded:', this.complaint);
          
          // Update form values immediately
          this.updateFormValues();
          
          // Update again after view is rendered
          setTimeout(() => {
            this.updateFormValues();
          }, 100);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading complaint details:', error);
        this.isLoading = false;
      }
    });
  }

  checkEditPermissions(): void {
    if (!this.complaint || !this.currentAdmin) {
      this.canEdit = false;
      return;
    }

    // If complaint is not assigned to anyone, any admin can edit
    if (!this.complaint.assignedTo) {
      this.canEdit = true;
      return;
    }

    // If complaint is assigned to current admin, they can edit
    if (this.complaint.assignedTo.id === this.currentAdmin.id) {
      this.canEdit = true;
      return;
    }

    // Otherwise, cannot edit
    this.canEdit = false;
  }

  loadAdmins(): void {
    this.adminService.getAdmins().subscribe({
      next: (response) => {
        if (response.success) {
          this.admins = response.data;
        }
      },
      error: (error) => {
        console.error('Error loading admins:', error);
      }
    });
  }

  updateFormValues(): void {
    if (this.complaint) {
      console.log('Updating form with complaint data:', this.complaint);
      
      // Set selected values
      this.selectedStatus = this.complaint.status || '';
      this.selectedPriority = this.complaint.priority || '';
      
      // Update form values
      this.adminForm.setValue({
        status: this.complaint.status || '',
        priority: this.complaint.priority || '',
        assignedTo: this.complaint.assignedTo?.id || '',
        resolution: this.complaint.resolution || ''
      });
      
      this.cdr.detectChanges();
      console.log('Form updated - selectedStatus:', this.selectedStatus);
    }
  }

  updateComplaint(): void {
    if (this.adminForm.valid && this.complaint) {
      this.isSubmitting = true;
      const updateData = this.adminForm.value;
      
      // Determine who to assign to
      let assignToId = updateData.assignedTo;
      
      // If no one is assigned and no specific assignment, assign to current admin
      if (!this.complaint.assignedTo && !assignToId && this.currentAdmin) {
        assignToId = this.currentAdmin.id;
      }
      
      // Auto-progress status logic
      let finalStatus = updateData.status;
      
      // If complaint is OPEN and admin is taking action but keeping it as OPEN, automatically set to IN_PROGRESS
      if (this.complaint.status === 'OPEN' && updateData.status === 'OPEN') {
        finalStatus = 'IN_PROGRESS';
        console.log('Auto-progressing complaint from OPEN to IN_PROGRESS');
      }
      
      // Update complaint resolution
      this.adminService.updateComplaintResolution(this.complaint.complaintId, {
        status: finalStatus,
        priority: updateData.priority,
        resolution: updateData.resolution
      }).subscribe({
        next: (response) => {
          if (response.success) {
            // If we need to assign the complaint
            if (assignToId) {
              this.adminService.assignComplaint(this.complaint!.complaintId, assignToId).subscribe({
                next: (assignResponse) => {
                  this.loadComplaintDetails(this.complaint!.complaintId.toString());
                  this.isSubmitting = false;
                },
                error: (error) => {
                  console.error('Error assigning complaint:', error);
                  this.isSubmitting = false;
                }
              });
            } else {
              this.loadComplaintDetails(this.complaint!.complaintId.toString());
              this.isSubmitting = false;
            }
          } else {
            this.isSubmitting = false;
          }
        },
        error: (error) => {
          console.error('Error updating complaint:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  isComplaintClosed(): boolean {
    return this.complaint?.status === 'RESOLVED' || this.complaint?.status === 'CLOSED';
  }

  goBack(): void {
    this.router.navigate(['/admin/complaints']);
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

  formatDoctorName(doctor: any): string {
    if (!doctor) return 'Unknown Doctor';
    
    // Try different possible data structures
    if (doctor.user?.firstname && doctor.user?.lastname) {
      return `Dr. ${doctor.user.firstname} ${doctor.user.lastname}`;
    }
    
    if (doctor.firstName && doctor.lastName) {
      return `Dr. ${doctor.firstName} ${doctor.lastName}`;
    }
    
    if (doctor.firstname && doctor.lastname) {
      return `Dr. ${doctor.firstname} ${doctor.lastname}`;
    }
    
    if (doctor.name) {
      return `Dr. ${doctor.name}`;
    }
    
    console.log('Doctor data structure:', doctor);
    return 'Unknown Doctor';
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'OPEN':
        return 'badge--red';
      case 'IN_PROGRESS':
        return 'badge--yellow';
      case 'RESOLVED':
        return 'badge--green';
      case 'CLOSED':
        return 'badge--gray';
      default:
        return 'badge--gray';
    }
  }

  getPriorityBadgeClass(priority: string): string {
    switch (priority) {
      case 'LOW':
        return 'badge--green';
      case 'MEDIUM':
        return 'badge--yellow';
      case 'HIGH':
        return 'badge--orange';
      case 'URGENT':
        return 'badge--red';
      default:
        return 'badge--gray';
    }
  }

  debugFormState(): void {
    console.log('=== DEBUG FORM STATE ===');
    console.log('Complaint status:', this.complaint?.status);
    console.log('Selected status:', this.selectedStatus);
    console.log('Form status value:', this.adminForm.get('status')?.value);
    console.log('Form values:', this.adminForm.value);
    console.log('========================');
  }
}