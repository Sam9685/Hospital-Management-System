import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule, SlicePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService, Complaint, SearchFilters, PaginatedResponse } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-admin-complaints',
  templateUrl: './admin-complaints.component.html',
  styleUrls: ['./admin-complaints.component.css'],
  imports: [CommonModule, ReactiveFormsModule],
  standalone: true
})
export class AdminComplaintsComponent implements OnInit {
  @ViewChild('editModal') editModal!: TemplateRef<any>;
  @ViewChild('deleteModal') deleteModal!: TemplateRef<any>;

  complaints: Complaint[] = [];
  paginatedResponse: PaginatedResponse<Complaint> | null = null;
  isLoading = false;
  searchTerm = '';
  searchSubject = new Subject<string>();
  currentAdmin: any = null;
  
  // Make Math available in template
  Math = Math;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  
  // Sorting
  sortBy = 'createdAt';
  sortDir: 'asc' | 'desc' = 'desc';
  
  // Filters
  showFilters = false;
  filterForm: FormGroup;
  selectedCategory = '';
  selectedStatus = '';
  selectedPriority = '';
  
  // Edit/Delete
  selectedComplaint: Complaint | null = null;
  showEditModal = false;
  showDeleteModal = false;
  editForm: FormGroup;
  isSubmitting = false;
  
  // Table columns
  columns = [
    { key: 'complaintId', label: 'ID', sortable: true },
    { key: 'title', label: 'Title', sortable: true },
    { key: 'patient', label: 'Patient', sortable: true },
    { key: 'category', label: 'Category', sortable: true },
    { key: 'priority', label: 'Priority', sortable: true },
    { key: 'status', label: 'Status', sortable: true },
    // { key: 'assignedTo', label: 'Assigned To', sortable: true },
    { key: 'createdAt', label: 'Created', sortable: true },
    { key: 'actions', label: 'Actions', sortable: false }
  ];

  categories = [
    { value: '', label: 'All Categories' },
    { value: 'TREATMENT_ISSUE', label: 'Treatment Issue' },
    { value: 'SERVICE_ISSUE', label: 'Service Issue' },
    { value: 'BILLING_ISSUE', label: 'Billing Issue' },
    { value: 'STAFF_ISSUE', label: 'Staff Issue' },
    { value: 'FACILITY_ISSUE', label: 'Facility Issue' },
    { value: 'OTHER', label: 'Other' }
  ];

  statuses = [
    { value: '', label: 'All Status' },
    { value: 'OPEN', label: 'Open' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'RESOLVED', label: 'Resolved' },
    { value: 'CLOSED', label: 'Closed' },
  ];

  priorities = [
    { value: '', label: 'All Priorities' },
    { value: 'LOW', label: 'Low' },
    { value: 'MEDIUM', label: 'Medium' },
    { value: 'HIGH', label: 'High' },
    { value: 'CRITICAL', label: 'Critical' }
  ];

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.filterForm = this.fb.group({
      category: [''],
      status: [''],
      priority: ['']
    });

    this.editForm = this.fb.group({
      status: ['OPEN', [Validators.required]],
      priority: ['MEDIUM', [Validators.required]],
      resolutionNotes: [''],
      assignedTo: ['']
    });

    // Setup search debounce
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.searchTerm = searchTerm;
      this.currentPage = 0;
      this.loadComplaints();
    });

    // Subscribe to form changes to keep state in sync
    this.filterForm.valueChanges.subscribe(value => {
      console.log('Form value changed:', value);
    });
  }

  ngOnInit(): void {
    this.currentAdmin = this.authService.getCurrentUser();
    this.syncFormWithState();
    this.loadComplaints();
  }

  // Sync form values with current state
  syncFormWithState(): void {
    this.filterForm.patchValue({
      category: this.selectedCategory,
      status: this.selectedStatus,
      priority: this.selectedPriority
    });
    console.log('Form synced with state:', {
      category: this.selectedCategory,
      status: this.selectedStatus,
      priority: this.selectedPriority
    });
  }

  loadComplaints(): void {
    this.isLoading = true;
    
    // Check authentication before making API call
    if (!this.authService.isLoggedIn()) {
      console.error('User not authenticated');
      this.isLoading = false;
      return;
    }
    
    const token = this.authService.getToken();
    console.log('Current token for complaints:', token ? `${token.substring(0, 20)}...` : 'No token');
    
    const filters: SearchFilters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      title: this.searchTerm || undefined,
      category: this.selectedCategory || undefined,
      status: this.selectedStatus || undefined,
      priority: this.selectedPriority || undefined
    };

    console.log('Loading complaints with filters:', filters);
    this.adminService.getComplaints(filters).subscribe({
      next: (response) => {
        console.log('Complaints response:', response);
        if (response.success) {
          this.paginatedResponse = response.data;
          this.complaints = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        } else {
          console.error('API returned error:', response.message);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading complaints:', error);
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
    this.loadComplaints();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadComplaints();
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = +target.value;
    this.pageSize = size;
    this.currentPage = 0;
    this.loadComplaints();
  }

  applyFilters(): void {
    this.selectedCategory = this.filterForm.get('category')?.value || '';
    this.selectedStatus = this.filterForm.get('status')?.value || '';
    this.selectedPriority = this.filterForm.get('priority')?.value || '';
    this.currentPage = 0;
    console.log('Applying filters:', {
      category: this.selectedCategory,
      status: this.selectedStatus,
      priority: this.selectedPriority
    });
    this.loadComplaints();
  }

  // Add method to handle individual filter changes
  onFilterChange(): void {
    this.selectedCategory = this.filterForm.get('category')?.value || '';
    this.selectedStatus = this.filterForm.get('status')?.value || '';
    this.selectedPriority = this.filterForm.get('priority')?.value || '';
    
    console.log('Filter changed:', {
      category: this.selectedCategory,
      status: this.selectedStatus,
      priority: this.selectedPriority
    });
    
    this.currentPage = 0;
    this.loadComplaints();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedCategory = '';
    this.selectedStatus = '';
    this.selectedPriority = '';
    this.currentPage = 0;
    console.log('Filters cleared');
    this.loadComplaints();
  }

  // Debug method to check form state
  debugFormState(): void {
    console.log('Current form state:', {
      formValue: this.filterForm.value,
      selectedCategory: this.selectedCategory,
      selectedStatus: this.selectedStatus,
      selectedPriority: this.selectedPriority
    });
  }

  filterAvailableToMe(): void {
    if (!this.currentAdmin) return;
    
    // Set filter to show only complaints available to current admin
    this.filterForm.patchValue({
      category: '',
      status: '',
      priority: '',
      assignedTo: this.currentAdmin.id // This will be handled in the API call
    });
    
    this.currentPage = 0;
    this.loadComplaintsAvailableToMe();
  }

  loadComplaintsAvailableToMe(): void {
    this.isLoading = true;
    
    const params = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      assignedTo: this.currentAdmin?.id || 'unassigned'
    };

    this.adminService.getComplaintsAvailableToAdmin(params).subscribe({
      next: (response) => {
        if (response.success) {
          this.complaints = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading available complaints:', error);
        this.isLoading = false;
      }
    });
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  editComplaint(complaint: Complaint): void {
    if (complaint.deletedAt) {
      return; // Don't allow editing deleted complaints
    }
    this.selectedComplaint = complaint;
    this.showEditModal = true;
    this.editForm.patchValue({
      status: complaint.status,
      priority: complaint.priority,
      resolutionNotes: complaint.resolutionNotes || '',
      assignedTo: complaint.assignedTo?.id || ''
    });
  }

  viewComplaint(complaint: Complaint): void {
    this.router.navigate(['/admin/complaints', complaint.complaintId]);
  }

  updateComplaint(): void {
    if (this.editForm.valid && this.selectedComplaint) {
      this.isSubmitting = true;
      const updateData = this.editForm.value;
      
      this.adminService.updateComplaint(this.selectedComplaint.complaintId, updateData).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadComplaints();
            this.closeEditModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error updating complaint:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  updateComplaintStatus(complaint: Complaint, status: string): void {
    this.isSubmitting = true;
      this.adminService.updateComplaintStatus(complaint.complaintId, status).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadComplaints();
        }
        this.isSubmitting = false;
      },
      error: (error) => {
        console.error('Error updating complaint status:', error);
        this.isSubmitting = false;
      }
    });
  }

  deleteComplaint(complaint: Complaint): void {
    if (complaint.deletedAt) {
      return; // Don't allow deleting already deleted complaints
    }
    this.selectedComplaint = complaint;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.selectedComplaint) {
      this.isSubmitting = true;
      this.adminService.deleteComplaint(this.selectedComplaint.complaintId).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadComplaints();
            this.closeDeleteModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error deleting complaint:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  closeEditModal(): void {
    this.selectedComplaint = null;
    this.showEditModal = false;
    this.editForm.reset();
  }

  closeDeleteModal(): void {
    this.selectedComplaint = null;
    this.showDeleteModal = false;
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'OPEN':
        return 'badge--blue';
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
      case 'CRITICAL':
        return 'badge--red';
      default:
        return 'badge--gray';
    }
  }

  getCategoryBadgeClass(category: string): string {
    switch (category) {
      case 'TREATMENT_ISSUE':
        return 'badge--red';
      case 'SERVICE_ISSUE':
        return 'badge--blue';
      case 'BILLING_ISSUE':
        return 'badge--purple';
      case 'STAFF_ISSUE':
        return 'badge--orange';
      case 'FACILITY_ISSUE':
        return 'badge--green';
      case 'OTHER':
        return 'badge--gray';
      default:
        return 'badge--gray';
    }
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
}