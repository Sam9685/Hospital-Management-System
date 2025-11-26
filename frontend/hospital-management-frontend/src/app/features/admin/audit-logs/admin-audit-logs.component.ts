import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule, JsonPipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { AdminService, AuditLog, SearchFilters, PaginatedResponse } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-admin-audit-logs',
  templateUrl: './admin-audit-logs.component.html',
  styleUrls: ['./admin-audit-logs.component.css'],
  imports: [CommonModule, ReactiveFormsModule, JsonPipe],
  standalone: true
})
export class AdminAuditLogsComponent implements OnInit {
  @ViewChild('detailsModal') detailsModal!: TemplateRef<any>;

  auditLogs: AuditLog[] = [];
  paginatedResponse: PaginatedResponse<AuditLog> | null = null;
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
  sortBy = 'createdAt';
  sortDir: 'asc' | 'desc' = 'desc';
  
  // Filters
  showFilters = false;
  filterForm: FormGroup;
  selectedAction = '';
  selectedTable = '';
  selectedUser = '';
  selectedDateFrom = '';
  selectedDateTo = '';
  selectedStatus = '';
  
  // Date validation properties
  minToDate = '';
  maxFromDate = '';
  
  // View Details
  selectedAuditLog: AuditLog | null = null;
  
  // Table columns
  columns = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'user', label: 'User', sortable: true },
    { key: 'action', label: 'Action', sortable: true },
    { key: 'tableName', label: 'Table', sortable: true },
    { key: 'recordId', label: 'Record ID', sortable: true },
    { key: 'ipAddress', label: 'IP Address', sortable: true },
    { key: 'createdAt', label: 'Timestamp', sortable: true },
    { key: 'actions', label: 'Actions', sortable: false }
  ];

  actions = [
    { value: '', label: 'All Actions' },
    { value: 'INSERT', label: 'Insert' },
    { value: 'UPDATE', label: 'Update' },
    { value: 'DELETE', label: 'Delete' },
    { value: 'SELECT', label: 'Select' }
  ];

  tables = [
    { value: '', label: 'All Tables' },
    { value: 'users', label: 'Users' },
    { value: 'doctors', label: 'Doctors' },
    { value: 'appointments', label: 'Appointments' },
    { value: 'complaints', label: 'Complaints' },
    { value: 'payments', label: 'Payments' }
  ];

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      action: [''],
      tableName: [''],
      userId: [''],
      dateFrom: [''],
      dateTo: [''],
      status: ['']
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
      this.loadAuditLogs();
    });
  }

  ngOnInit(): void {
    this.loadAuditLogs();
  }

  loadAuditLogs(): void {
    this.isLoading = true;
    
    // Check authentication before making API call
    if (!this.authService.isLoggedIn()) {
      console.error('User not authenticated');
      this.isLoading = false;
      return;
    }
    
    const token = this.authService.getToken();
    console.log('Current token for audit logs:', token ? `${token.substring(0, 20)}...` : 'No token');
    
    const filters: SearchFilters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      action: this.selectedAction || undefined,
      tableName: this.selectedTable || undefined,
      userId: this.selectedUser || undefined,
      fromDate: this.selectedDateFrom || undefined,
      toDate: this.selectedDateTo || undefined,
      status: this.selectedStatus || undefined
    };

    console.log('Loading audit logs with filters:', filters);
    this.adminService.getAuditLogs(filters).subscribe({
      next: (response) => {
        console.log('Audit logs response:', response);
        if (response.success) {
          this.paginatedResponse = response.data;
          this.auditLogs = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        } else {
          console.error('API returned error:', response.message);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading audit logs:', error);
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
    this.loadAuditLogs();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadAuditLogs();
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = +target.value;
    this.pageSize = size;
    this.currentPage = 0;
    this.loadAuditLogs();
  }

  applyFilters(): void {
    this.selectedAction = this.filterForm.get('action')?.value || '';
    this.selectedTable = this.filterForm.get('tableName')?.value || '';
    this.selectedUser = this.filterForm.get('userId')?.value || '';
    this.selectedDateFrom = this.filterForm.get('dateFrom')?.value || '';
    this.selectedDateTo = this.filterForm.get('dateTo')?.value || '';
    this.selectedStatus = this.filterForm.get('status')?.value || '';
    this.currentPage = 0;
    this.loadAuditLogs();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedAction = '';
    this.selectedTable = '';
    this.selectedUser = '';
    this.selectedDateFrom = '';
    this.selectedDateTo = '';
    this.selectedStatus = '';
    this.currentPage = 0;
    this.loadAuditLogs();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  viewDetails(auditLog: AuditLog): void {
    this.selectedAuditLog = auditLog;
  }

  closeDetailsModal(): void {
    this.selectedAuditLog = null;
  }

  getActionBadgeClass(action: string): string {
    switch (action) {
      case 'INSERT':
        return 'badge--green';
      case 'UPDATE':
        return 'badge--blue';
      case 'DELETE':
        return 'badge--red';
      case 'SELECT':
        return 'badge--gray';
      default:
        return 'badge--gray';
    }
  }

  getTableBadgeClass(table: string): string {
    switch (table.toLowerCase()) {
      case 'users':
        return 'badge--blue';
      case 'doctors':
        return 'badge--green';
      case 'appointments':
        return 'badge--purple';
      case 'complaints':
        return 'badge--orange';
      case 'payments':
        return 'badge--yellow';
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

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
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

  parseJsonSafely(jsonString: string): any {
    try {
      return JSON.parse(jsonString);
    } catch {
      return jsonString;
    }
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
