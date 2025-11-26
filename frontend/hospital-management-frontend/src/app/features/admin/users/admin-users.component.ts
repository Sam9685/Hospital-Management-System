import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AdminService, User, SearchFilters, PaginatedResponse } from '../../../core/services/admin.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { CustomValidators } from '../../../shared/validators/custom-validators';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css'],
  imports: [CommonModule, ReactiveFormsModule],
  standalone: true
})
export class AdminUsersComponent implements OnInit {
  @ViewChild('editModal') editModal!: TemplateRef<any>;
  @ViewChild('deleteModal') deleteModal!: TemplateRef<any>;

  users: User[] = [];
  paginatedResponse: PaginatedResponse<User> | null = null;
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
  sortBy = 'id';
  sortDir: 'asc' | 'desc' = 'asc';
  
  // Filters
  showFilters = false;
  filterForm: FormGroup;
  selectedRole = '';
  selectedGender = '';
  selectedStatus = '';
  
  // Edit/Delete/Add
  selectedUser: User | null = null;
  showEditModal = false;
  showDeleteModal = false;
  showAddModal = false;
  editForm: FormGroup;
  addForm: FormGroup;
  isSubmitting = false;
  
  // Table columns
  columns = [
    { key: 'id', label: 'ID', sortable: true },
    { key: 'name', label: 'Name', sortable: true },
    { key: 'email', label: 'Email', sortable: true },
    { key: 'role', label: 'Role', sortable: true },
    { key: 'contact', label: 'Contact', sortable: false },
    { key: 'city', label: 'City', sortable: true },
    { key: 'active', label: 'Status', sortable: true },
    { key: 'createdAt', label: 'Created', sortable: true },
    { key: 'actions', label: 'Actions', sortable: false }
  ];

  roles = [
    { value: '', label: 'All Roles' },
    { value: 'ADMIN', label: 'Admin' },
    { value: 'PATIENT', label: 'Patient' }
  ];

  availableRoles = [
    { value: 'PATIENT', label: 'Patient' }
  ];

  genders = [
    { value: '', label: 'All Genders' },
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
    { value: 'OTHER', label: 'Other' }
  ];

  statuses = [
    { value: '', label: 'All Status' },
    { value: 'true', label: 'Active' },
    { value: 'false', label: 'Inactive' }
  ];

  countryCodes = [
    { value: '+91', label: '+91 (India)' },
    { value: '+1', label: '+1 (USA/Canada)' },
    { value: '+44', label: '+44 (UK)' },
    { value: '+61', label: '+61 (Australia)' },
    { value: '+49', label: '+49 (Germany)' },
    { value: '+33', label: '+33 (France)' },
    { value: '+81', label: '+81 (Japan)' },
    { value: '+86', label: '+86 (China)' },
    { value: '+55', label: '+55 (Brazil)' },
    { value: '+7', label: '+7 (Russia)' }
  ];

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private fb: FormBuilder,
    private toastService: ToastService
  ) {
    this.filterForm = this.fb.group({
      role: [''],
      gender: [''],
      active: ['']
    });

    this.editForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      lastname: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      email: ['', [Validators.required, CustomValidators.email]], // Email is disabled in edit mode
      contact: ['', [Validators.required, CustomValidators.indianPhoneNumber]],
      countryCode: ['+91', [Validators.required]],
      gender: ['', [Validators.required]],
      birthdate: ['', [Validators.required, CustomValidators.pastDate, CustomValidators.maxAge(100)]],
      address: ['', [Validators.required, Validators.minLength(10)]],
      city: ['', [Validators.required, CustomValidators.textOnly]],
      state: ['', [Validators.required, CustomValidators.textOnly]],
      country: ['', [Validators.required, CustomValidators.textOnly]],
      postalCode: ['', [Validators.required, CustomValidators.postalCode]],
      bloodGroup: ['', [CustomValidators.bloodGroup]],
      emergencyContactName: ['', [CustomValidators.textOnly]],
      emergencyContactNum: ['', [CustomValidators.indianPhoneNumber]]
    });

    this.addForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      lastname: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      username: ['', [Validators.required, CustomValidators.username]],
      email: ['', [Validators.required, CustomValidators.email]],
      password: ['', [Validators.required, CustomValidators.passwordStrength]],
      // role is set to 'PATIENT' automatically, no need for form field
      contact: ['', [Validators.required, CustomValidators.indianPhoneNumber]],
      countryCode: ['+91', [Validators.required]],
      gender: ['', [Validators.required]],
      birthdate: ['', [Validators.required, CustomValidators.pastDate, CustomValidators.maxAge(100)]],
      address: ['', [Validators.required, Validators.minLength(10)]],
      city: ['', [Validators.required, CustomValidators.textOnly]],
      state: ['', [Validators.required, CustomValidators.textOnly]],
      country: ['', [Validators.required, CustomValidators.textOnly]],
      postalCode: ['', [Validators.required, CustomValidators.postalCode]],
      bloodGroup: ['', [CustomValidators.bloodGroup]],
      emergencyContactName: ['', [CustomValidators.textOnly]],
      emergencyContactNum: ['', [CustomValidators.indianPhoneNumber]]
    });

    // Setup search debounce
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.searchTerm = searchTerm;
      this.currentPage = 0;
      this.loadUsers();
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    this.setAvailableRoles();
  }

  setAvailableRoles(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      if (currentUser.role === 'SUPERADMIN') {
        this.availableRoles = [
          { value: 'PATIENT', label: 'Patient' },
          { value: 'ADMIN', label: 'Admin' }
        ];
      } else if (currentUser.role === 'ADMIN') {
        this.availableRoles = [
          { value: 'PATIENT', label: 'Patient' }
        ];
      }
    }
  }

  loadUsers(): void {
    this.isLoading = true;
    const filters: SearchFilters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      name: this.searchTerm || undefined,
      email: this.searchTerm || undefined,
      role: this.selectedRole || undefined,
      gender: this.selectedGender || undefined,
      active: this.selectedStatus || undefined
    };

    this.adminService.getUsers(filters).subscribe({
      next: (response) => {
        console.log('Users response:', response);
        if (response.success) {
          this.paginatedResponse = response.data;
          this.users = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        } else {
          console.error('API returned error:', response.message);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.toastService.showError('Failed to load users: ' + (error.error?.message || error.message || 'Unknown error'));
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
    this.loadUsers();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadUsers();
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = +target.value;
    this.pageSize = size;
    this.currentPage = 0;
    this.loadUsers();
  }

  applyFilters(): void {
    this.selectedRole = this.filterForm.get('role')?.value || '';
    this.selectedGender = this.filterForm.get('gender')?.value || '';
    this.selectedStatus = this.filterForm.get('active')?.value || '';
    this.currentPage = 0;
    this.loadUsers();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedRole = '';
    this.selectedGender = '';
    this.selectedStatus = '';
    this.currentPage = 0;
    this.loadUsers();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  editUser(user: User): void {
    if (user.deletedAt) {
      return; // Don't allow editing deleted users
    }
    this.selectedUser = user;
    this.showEditModal = true;
    this.editForm.patchValue({
      firstname: user.firstname || '',
      lastname: user.lastname || '',
      email: user.email,
      contact: user.contact || '',
      countryCode: user.countryCode || '+91',
      gender: user.gender || '',
      birthdate: user.birthdate || '',
      address: user.address || '',
      city: user.city || '',
      state: user.state || '',
      country: user.country || '',
      postalCode: user.postalCode || '',
      bloodGroup: user.bloodGroup || '',
      emergencyContactName: user.emergencyContactName || '',
      emergencyContactNum: user.emergencyContactNum || ''
    });
  }

  updateUser(): void {
    if (this.editForm.valid && this.selectedUser) {
      this.isSubmitting = true;
      const updateData = this.editForm.value;
      
      
      this.adminService.updateUser(this.selectedUser.id, updateData).subscribe({
        next: (response) => {
          if (response.success) {
            // Update the selectedUser object with the new data
            this.selectedUser = { ...this.selectedUser, ...updateData };
            // Update the name field if firstname or lastname was updated
            if (updateData.firstname || updateData.lastname) {
              const firstName = updateData.firstname || this.selectedUser?.firstname || '';
              const lastName = updateData.lastname || this.selectedUser?.lastname || '';
              if (this.selectedUser) {
                this.selectedUser.name = `${firstName} ${lastName}`.trim();
              }
            }
            this.loadUsers();
            this.closeEditModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error updating user:', error);
          this.toastService.showError('Failed to update user: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isSubmitting = false;
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (user.deletedAt) {
      return; // Don't allow deleting already deleted users
    }
    this.selectedUser = user;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.selectedUser) {
      this.isSubmitting = true;
      this.adminService.deleteUser(this.selectedUser.id).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadUsers();
            this.closeDeleteModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          this.toastService.showError('Failed to delete user: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isSubmitting = false;
        }
      });
    }
  }

  closeEditModal(): void {
    this.selectedUser = null;
    this.showEditModal = false;
    this.editForm.reset();
  }

  closeDeleteModal(): void {
    this.selectedUser = null;
    this.showDeleteModal = false;
  }

  addUser(): void {
    this.showAddModal = true;
    this.addForm.reset();
  }

  createUser(): void {
    if (this.addForm.valid) {
      this.isSubmitting = true;
      const userData = this.addForm.value;
      // Set role to PATIENT automatically and create full name from first and last name
      userData.role = 'PATIENT';
      userData.name = `${userData.firstname} ${userData.lastname}`;
      
      this.adminService.createUser(userData).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadUsers();
            this.closeAddModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error creating user:', error);
          this.toastService.showError('Failed to create user: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isSubmitting = false;
        }
      });
    }
  }

  closeAddModal(): void {
    this.showAddModal = false;
    this.addForm.reset();
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'SUPERADMIN':
        return 'badge--purple';
      case 'ADMIN':
        return 'badge--blue';
      case 'PATIENT':
        return 'badge--green';
      default:
        return 'badge--gray';
    }
  }

  getGenderBadgeClass(gender: string): string {
    switch (gender) {
      case 'MALE':
        return 'badge--blue';
      case 'FEMALE':
        return 'badge--pink';
      case 'OTHER':
        return 'badge--purple';
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

  toggleUserStatus(user: User): void {
    // Prevent changing status of admin users
    if (this.isAdminUser(user)) {
      return;
    }
    
    const newStatus = !user.active;
    this.adminService.updateUser(user.id, { active: newStatus }).subscribe({
      next: (response) => {
        if (response.success) {
          user.active = newStatus;
          this.loadUsers();
        }
      },
      error: (error) => {
        console.error('Error updating user status:', error);
        this.toastService.showError('Failed to update user status: ' + (error.error?.message || error.message || 'Unknown error'));
      }
    });
  }

  // Input event handlers for live validation and character restrictions
  onTextInput(event: Event, controlName: string): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const textOnlyValue = value.replace(/[^a-zA-Z\s]/g, '');
    if (value !== textOnlyValue) {
      target.value = textOnlyValue;
      this.addForm.get(controlName)?.setValue(textOnlyValue);
      this.editForm.get(controlName)?.setValue(textOnlyValue);
    }
  }

  onNumberInput(event: Event, controlName: string): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const numberOnlyValue = value.replace(/\D/g, '');
    if (value !== numberOnlyValue) {
      target.value = numberOnlyValue;
      this.addForm.get(controlName)?.setValue(numberOnlyValue);
      this.editForm.get(controlName)?.setValue(numberOnlyValue);
    }
  }

  onEmailInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const value = target.value.toLowerCase();
    if (target.value !== value) {
      target.value = value;
      this.addForm.get('email')?.setValue(value);
      this.editForm.get('email')?.setValue(value);
    }
  }

  onEmailKeyPress(event: KeyboardEvent): void {
    const inputChar = String.fromCharCode(event.charCode);
    if (/[A-Z]/.test(inputChar)) {
      event.preventDefault();
      const target = event.target as HTMLInputElement;
      const currentValue = target.value;
      const newValue = currentValue + inputChar.toLowerCase();
      target.value = newValue;
      this.addForm.get('email')?.setValue(newValue);
      this.editForm.get('email')?.setValue(newValue);
    }
  }

  onPostalCodeInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const numberOnlyValue = value.replace(/\D/g, '');
    const limitedValue = numberOnlyValue.substring(0, 6);
    if (target.value !== limitedValue) {
      target.value = limitedValue;
      this.addForm.get('postalCode')?.setValue(limitedValue);
      this.editForm.get('postalCode')?.setValue(limitedValue);
    }
  }

  onContactInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const numberOnlyValue = value.replace(/\D/g, '');
    const limitedValue = numberOnlyValue.substring(0, 10);
    if (target.value !== limitedValue) {
      target.value = limitedValue;
      this.addForm.get('contact')?.setValue(limitedValue);
      this.editForm.get('contact')?.setValue(limitedValue);
    }
  }

  onEmergencyContactInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const numberOnlyValue = value.replace(/\D/g, '');
    const limitedValue = numberOnlyValue.substring(0, 10);
    if (target.value !== limitedValue) {
      target.value = limitedValue;
      this.addForm.get('emergencyContactNum')?.setValue(limitedValue);
      this.editForm.get('emergencyContactNum')?.setValue(limitedValue);
    }
  }

  onUsernameInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const usernameValue = value.replace(/[^a-zA-Z0-9._-]/g, '');
    if (target.value !== usernameValue) {
      target.value = usernameValue;
      this.addForm.get('username')?.setValue(usernameValue);
    }
  }

  // Prevent non-text characters in name fields
  onNameKeyPress(event: KeyboardEvent): void {
    const pattern = /^[a-zA-Z\s]*$/;
    const inputChar = String.fromCharCode(event.charCode);
    if (!pattern.test(inputChar) && event.charCode !== 0) {
      event.preventDefault();
    }
  }

  // Prevent non-digit characters in number fields
  onNumberKeyPress(event: KeyboardEvent): void {
    const pattern = /^[0-9]*$/;
    const inputChar = String.fromCharCode(event.charCode);
    if (!pattern.test(inputChar) && event.charCode !== 0) {
      event.preventDefault();
    }
  }

  // Get maximum date for birth date (today)
  getMaxDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  // Check if current user can edit/delete the target user
  canEditUser(user: User): boolean {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) return false;
    
    // Admin users cannot edit other admin users
    if (user.role === 'ADMIN') {
      return false;
    }
    
    return true;
  }

  canDeleteUser(user: User): boolean {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) return false;
    
    // Admin users cannot delete other admin users
    if (user.role === 'ADMIN') {
      return false;
    }
    
    return true;
  }

  // Check if user is admin (for styling purposes)
  isAdminUser(user: User): boolean {
    return user.role === 'ADMIN';
  }

  // Get display name for user (firstname + lastname)
  getUserDisplayName(user: User): string {
    if (user.firstname && user.lastname) {
      return `${user.firstname} ${user.lastname}`;
    }
    // Fallback to name field if firstname/lastname not available
    return user.name || 'Unknown User';
  }
}
