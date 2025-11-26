import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService, Doctor, User, SearchFilters, PaginatedResponse } from '../../../core/services/admin.service';
import { ToastService } from '../../../core/services/toast.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { CustomValidators } from '../../../shared/validators/custom-validators';

@Component({
  selector: 'app-admin-doctors',
  templateUrl: './admin-doctors.component.html',
  styleUrls: ['./admin-doctors.component.css'],
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  standalone: true
})
export class AdminDoctorsComponent implements OnInit {
  @ViewChild('editModal') editModal!: TemplateRef<any>;
  @ViewChild('deleteModal') deleteModal!: TemplateRef<any>;

  doctors: Doctor[] = [];
  paginatedResponse: PaginatedResponse<Doctor> | null = null;
  isLoading = false;
  searchTerm = '';
  searchSubject = new Subject<string>();
  
  // Make Math available in template
  Math = Math;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  
  // Date restrictions
  maxDate: string = '';
  totalPages = 0;
  
  // Sorting
  sortBy = 'doctorId';
  sortDir: 'asc' | 'desc' = 'asc';
  
  // Filters
  showFilters = false;
  filterForm: FormGroup;
  selectedSpecialization = '';
  selectedStatus = '';
  
  // Edit/Delete/Add
  selectedDoctor: Doctor | null = null;
  showEditModal = false;
  showDeleteModal = false;
  showAddModal = false;
  editForm: FormGroup;
  addForm: FormGroup;
  isSubmitting = false;
  
  // Data for dropdowns
  specializations: any[] = [];
  
  // Table columns
  columns = [
    { key: 'doctorId', label: 'ID', sortable: true },
    { key: 'name', label: 'Doctor Name/Email', sortable: true },
    { key: 'specialization', label: 'Specialization', sortable: true },
    { key: 'licenseNumber', label: 'License', sortable: true },
    { key: 'qualification', label: 'Qualification', sortable: true },
    { key: 'consultationFee', label: 'Fee', sortable: true },
    { key: 'active', label: 'Status', sortable: true },
    { key: 'actions', label: 'Actions', sortable: false }
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
    private fb: FormBuilder,
    private toastService: ToastService
  ) {
    this.filterForm = this.fb.group({
      specialization: [''],
      active: ['']
    });

    this.editForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      lastName: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      email: ['', [Validators.required, CustomValidators.email]],
      contact: ['', [Validators.required, CustomValidators.indianPhoneNumber]],
      gender: ['', [Validators.required]],
      emergencyContactName: ['', [CustomValidators.textOnly]],
      emergencyContactNum: ['', [CustomValidators.indianPhoneNumber]],
      state: ['', [Validators.required, CustomValidators.textOnly]],
      city: ['', [Validators.required, CustomValidators.textOnly]],
      address: ['', [Validators.required, Validators.minLength(10)]],
      country: ['', [Validators.required, CustomValidators.textOnly]],
      countryCode: ['', [Validators.required]],
      postalCode: ['', [Validators.required, CustomValidators.postalCode]],
      bloodGroup: [''],
      profileUrl: [''],
      specializationId: ['', [Validators.required]],
      licenseNumber: ['', [Validators.required, CustomValidators.licenseNumber]],
      qualification: ['', [Validators.required, CustomValidators.textOnly]],
      bio: [''],
      consultationFee: [100, [Validators.required, Validators.min(100), Validators.max(50000)]],
      yearsOfExp: [0, [Validators.required, Validators.min(0), Validators.max(50), CustomValidators.experienceValidForJoiningDate('joiningDate')]],
      active: [true, [Validators.required]],
      joiningDate: ['', [Validators.required, CustomValidators.pastDate, CustomValidators.minDate(1960)]],
      // Slot management fields
      slotStartTime: ['09:00', [Validators.required]],
      slotEndTime: ['17:00', [Validators.required]],
      appointmentDuration: [30, [Validators.required, Validators.min(15), Validators.max(60)]],
      workingDays: ['MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY', [Validators.required]]
    });

    this.addForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      lastName: ['', [Validators.required, Validators.minLength(2), CustomValidators.textOnly]],
      email: ['', [Validators.required, CustomValidators.email]],
      password: ['', [Validators.required, CustomValidators.passwordStrength]],
      contact: ['', [Validators.required, CustomValidators.indianPhoneNumber]],
      gender: ['', [Validators.required]],
      emergencyContactName: ['', [CustomValidators.textOnly]],
      emergencyContactNum: ['', [CustomValidators.indianPhoneNumber]],
      state: ['', [Validators.required, CustomValidators.textOnly]],
      city: ['', [Validators.required, CustomValidators.textOnly]],
      address: ['', [Validators.required, Validators.minLength(10)]],
      country: ['', [Validators.required, CustomValidators.textOnly]],
      countryCode: ['', [Validators.required]],
      postalCode: ['', [Validators.required, CustomValidators.postalCode]],
      bloodGroup: [''],
      profileUrl: [''],
      specializationId: ['', [Validators.required]],
      licenseNumber: ['', [Validators.required, CustomValidators.licenseNumber]],
      qualification: ['', [Validators.required, CustomValidators.textOnly]],
      bio: [''],
      consultationFee: [100, [Validators.required, Validators.min(100), Validators.max(50000)]],
      yearsOfExp: [0, [Validators.required, Validators.min(0), Validators.max(50), CustomValidators.experienceValidForJoiningDate('joiningDate')]],
      joiningDate: ['', [Validators.required, CustomValidators.pastDate, CustomValidators.minDate(1960)]],
      active: [true],
      // Slot management fields
      slotStartTime: ['09:00', [Validators.required]],
      slotEndTime: ['17:00', [Validators.required]],
      appointmentDuration: [30, [Validators.required, Validators.min(15), Validators.max(60)]],
      workingDays: ['MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY', [Validators.required]]
    });

    // Add form value change debugging
    this.addForm.valueChanges.subscribe(value => {
      console.log('Form value changed:', value);
    });

    // Setup search debounce
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.searchTerm = searchTerm;
      this.currentPage = 0;
      this.loadDoctors();
    });
  }

  ngOnInit(): void {
    this.setDateRestrictions();
    this.loadDoctors();
    this.loadSpecializations();
    
    // Add change listeners for cross-field validation
    this.setupCrossFieldValidation();
  }

  // Setup cross-field validation between joining date and years of experience
  setupCrossFieldValidation(): void {
    // For edit form
    this.editForm.get('joiningDate')?.valueChanges.subscribe(() => {
      this.editForm.get('yearsOfExp')?.updateValueAndValidity();
      this.editForm.get('yearsOfExp')?.markAsTouched();
    });

    // For add form
    this.addForm.get('joiningDate')?.valueChanges.subscribe(() => {
      this.addForm.get('yearsOfExp')?.updateValueAndValidity();
      this.addForm.get('yearsOfExp')?.markAsTouched();
    });
  }

  // Handle joining date change to trigger validation
  onJoiningDateChange(): void {
    // This method is called from the template when joining date changes
    // The valueChanges subscription in setupCrossFieldValidation will handle the rest
  }



  // Working days helper methods
  isWorkingDaySelected(day: string): boolean {
    const workingDays = this.editForm.get('workingDays')?.value || '';
    if (!workingDays) return false;
    return workingDays.split(',').includes(day);
  }

  updateWorkingDaysEdit(event: any, day: string): void {
    const isChecked = event.target.checked;
    const currentDays = this.editForm.get('workingDays')?.value || '';
    let newDays: string[] = [];
    
    if (currentDays) {
      newDays = currentDays.split(',').filter((d: string) => d.trim() !== '');
    }
    
    if (isChecked) {
      if (!newDays.includes(day)) {
        newDays.push(day);
      }
    } else {
      newDays = newDays.filter((d: string) => d !== day);
    }
    
    this.editForm.get('workingDays')?.setValue(newDays.join(','));
    this.editForm.get('workingDays')?.markAsTouched();
  }

  setDateRestrictions(): void {
    const today = new Date();
    this.maxDate = today.toISOString().split('T')[0];
  }

  loadDoctors(): void {
    this.isLoading = true;
    const filters: SearchFilters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      name: this.searchTerm || undefined,
      specialization: this.selectedSpecialization || undefined,
      active: this.selectedStatus || undefined
    };

    console.log('Loading doctors with filters:', filters);
    this.adminService.getDoctors(filters).subscribe({
      next: (response) => {
        console.log('Doctors response:', response);
        if (response.success) {
          this.paginatedResponse = response.data;
          this.doctors = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        } else {
          console.error('API returned error:', response.message);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading doctors:', error);
        this.toastService.showError('Failed to load doctors: ' + (error.error?.message || error.message || 'Unknown error'));
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
    this.loadDoctors();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadDoctors();
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = +target.value;
    this.pageSize = size;
    this.currentPage = 0;
    this.loadDoctors();
  }

  applyFilters(): void {
    this.selectedSpecialization = this.filterForm.get('specialization')?.value || '';
    this.selectedStatus = this.filterForm.get('active')?.value || '';
    this.currentPage = 0;
    this.loadDoctors();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedSpecialization = '';
    this.selectedStatus = '';
    this.currentPage = 0;
    this.loadDoctors();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  editDoctor(doctor: Doctor): void {
    if (doctor.deletedAt) {
      return; // Don't allow editing deleted doctors
    }
    this.selectedDoctor = doctor;
    this.showEditModal = true;
    this.editForm.patchValue({
      firstName: doctor.firstName,
      lastName: doctor.lastName,
      email: doctor.email,
      contact: doctor.contact || '',
      gender: doctor.gender || '',
      emergencyContactName: doctor.emergencyContactName || '',
      emergencyContactNum: doctor.emergencyContactNum || '',
      state: doctor.state || '',
      city: doctor.city || '',
      address: doctor.address || '',
      country: doctor.country || '',
      countryCode: doctor.countryCode || '+91',
      postalCode: doctor.postalCode || '',
      bloodGroup: doctor.bloodGroup || '',
      profileUrl: doctor.profileUrl || '',
      specializationId: doctor.specialization.specializationId,
      licenseNumber: doctor.licenseNumber,
      qualification: doctor.qualification,
      bio: doctor.bio || '',
      consultationFee: doctor.consultationFee,
      yearsOfExp: doctor.yearsOfExp,
      active: doctor.active,
      joiningDate: doctor.joiningDate,
      // Slot management fields
      slotStartTime: doctor.slotStartTime || '09:00',
      slotEndTime: doctor.slotEndTime || '17:00',
      appointmentDuration: doctor.appointmentDuration || 30,
      workingDays: doctor.workingDays || 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY'
    });
    
    // Debug form state
    console.log('Edit form after patch:', this.editForm.value);
    console.log('Edit form valid:', this.editForm.valid);
    console.log('Edit form errors:', this.editForm.errors);
    
    // Mark all fields as touched to show validation errors
    Object.keys(this.editForm.controls).forEach(key => {
      this.editForm.get(key)?.markAsTouched();
    });
  }

  updateDoctor(): void {
    // Debug form validation
    console.log('=== UPDATE DOCTOR DEBUG ===');
    console.log('Form valid:', this.editForm.valid);
    console.log('Form value:', this.editForm.value);
    console.log('Form errors:', this.editForm.errors);
    
    // Check individual field validity
    Object.keys(this.editForm.controls).forEach(key => {
      const control = this.editForm.get(key);
      if (control && control.invalid) {
        console.log(`${key} is invalid:`, control.errors);
      }
    });
    
    if (this.editForm.valid && this.selectedDoctor) {
      this.isSubmitting = true;
      const updateData = this.editForm.value;
      
      // Remove critical fields that shouldn't be updated
      delete updateData.email;
      delete updateData.licenseNumber;
      
      // Convert consultation fee to number if it's a string
      if (updateData.consultationFee && typeof updateData.consultationFee === 'string') {
        updateData.consultationFee = parseFloat(updateData.consultationFee);
      }
      
      this.adminService.updateDoctor(this.selectedDoctor.doctorId, updateData).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadDoctors();
            this.closeEditModal();
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error updating doctor:', error);
          this.toastService.showError('Failed to update doctor: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isSubmitting = false;
        }
      });
    } else {
      console.log('Form is invalid, cannot update doctor');
    }
  }

  deleteDoctor(doctor: Doctor): void {
    if (doctor.deletedAt) {
      return; // Don't allow deleting already deleted doctors
    }
    this.selectedDoctor = doctor;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.selectedDoctor) {
      this.isSubmitting = true;
      this.adminService.deleteDoctor(this.selectedDoctor.doctorId).subscribe({
        next: (response) => {
          if (response.success) {
            this.loadDoctors();
            this.closeDeleteModal();
            
            // Show success message with cancellation info
            const cancelledAppointments = response.data?.cancelledAppointments || 0;
            const doctorName = response.data?.doctorName || this.getDoctorName(this.selectedDoctor!);
            
            if (cancelledAppointments > 0) {
              this.toastService.showSuccess(
                `Doctor ${doctorName} deleted successfully and ${cancelledAppointments} appointment${cancelledAppointments === 1 ? '' : 's'} cancelled`
              );
            } else {
              this.toastService.showSuccess(`Doctor ${doctorName} deleted successfully`);
            }
          }
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error deleting doctor:', error);
          this.toastService.showError('Failed to delete doctor: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isSubmitting = false;
        }
      });
    }
  }

  closeEditModal(): void {
    this.selectedDoctor = null;
    this.showEditModal = false;
    this.editForm.reset();
  }

  closeDeleteModal(): void {
    this.selectedDoctor = null;
    this.showDeleteModal = false;
  }


  loadSpecializations(): void {
    this.adminService.getSpecializations().subscribe({
      next: (response) => {
        console.log('Specializations API response:', response);
        if (response.success) {
          this.specializations = response.data;
          console.log('Loaded specializations:', this.specializations);
        } else {
          console.error('API returned error:', response.message);
        }
      },
      error: (error) => {
        console.error('Error loading specializations:', error);
        this.toastService.showError('Failed to load specializations: ' + (error.error?.message || error.message || 'Unknown error'));
      }
    });
  }

  addDoctor(): void {
    this.showAddModal = true;
    
    // Use setTimeout to ensure the form is properly initialized
    setTimeout(() => {
      // Reset form and immediately set default values
      this.addForm.reset({
        consultationFee: 100,
        active: true,
        slotStartTime: '09:00',
        slotEndTime: '17:00',
        appointmentDuration: 30,
        workingDays: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY'
      });
      console.log('Add form consultation fee after reset:', this.addForm.get('consultationFee')?.value);
    }, 0);
    
    console.log('Add doctor modal opened');
    
    // Load specializations when opening the modal
    this.loadSpecializations();
    
    console.log('Available specializations:', this.specializations);
    
    // Set default values
    const today = new Date().toISOString().split('T')[0]; // Get today's date in YYYY-MM-DD format
    this.addForm.patchValue({
      consultationFee: 0,
      yearsOfExp: 0,
      joiningDate: today, // Set today as default joining date
      slotStartTime: '09:00',
      slotEndTime: '17:00',
      appointmentDuration: 30,
      workingDays: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
      active: true
    });
    
    console.log('Form after reset and patch:', this.addForm.value);
  }

  createDoctor(): void {
    console.log('=== CREATE DOCTOR DEBUG ===');
    console.log('Form valid:', this.addForm.valid);
    console.log('Form value:', this.addForm.value);
    console.log('Form errors:', this.addForm.errors);
    
    // Check individual field validity
    console.log('specializationId valid:', this.addForm.get('specializationId')?.valid);
    console.log('licenseNumber valid:', this.addForm.get('licenseNumber')?.valid);
    console.log('qualification valid:', this.addForm.get('qualification')?.valid);
    console.log('joiningDate valid:', this.addForm.get('joiningDate')?.valid);
    console.log('consultationFee valid:', this.addForm.get('consultationFee')?.valid);
    console.log('yearsOfExp valid:', this.addForm.get('yearsOfExp')?.valid);
    
    // Check individual field values
    console.log('specializationId value:', this.addForm.get('specializationId')?.value);
    console.log('licenseNumber value:', this.addForm.get('licenseNumber')?.value);
    console.log('qualification value:', this.addForm.get('qualification')?.value);
    console.log('joiningDate value:', this.addForm.get('joiningDate')?.value);
    console.log('consultationFee value:', this.addForm.get('consultationFee')?.value);
    console.log('yearsOfExp value:', this.addForm.get('yearsOfExp')?.value);
    
    const formValue = this.addForm.value;
    
    // Validate that specializationId is not undefined or empty
    if (!formValue.specializationId || formValue.specializationId === 'undefined' || formValue.specializationId === '') {
      console.error('Specialization ID is undefined or invalid:', formValue.specializationId);
      alert('Please select a specialization');
      return;
    }
    
    // Note: userId is not needed for doctor creation as doctors are separate entities
    
    // Validate that joiningDate is not undefined or empty
    if (!formValue.joiningDate || formValue.joiningDate === 'undefined' || formValue.joiningDate === '') {
      console.error('Joining date is undefined or invalid:', formValue.joiningDate);
      alert('Please select a joining date');
      return;
    }
    
    if (this.addForm.valid) {
      this.isSubmitting = true;
      const doctorData = { ...this.addForm.value };
      
      // Convert consultation fee to number if it's a string
      if (doctorData.consultationFee && typeof doctorData.consultationFee === 'string') {
        doctorData.consultationFee = parseFloat(doctorData.consultationFee);
      }
      
      console.log('Sending doctor data:', doctorData);
      
      this.adminService.createDoctor(doctorData).subscribe({
        next: (response) => {
          console.log('Doctor creation response:', response);
          if (response.success) {
            this.loadDoctors();
            this.closeAddModal();
          }
          this.isSubmitting = false;
        },
      error: (error) => {
        console.error('Error creating doctor:', error);
        this.toastService.showError('Failed to create doctor: ' + (error.error?.message || error.message || 'Unknown error'));
        this.isSubmitting = false;
      }
      });
    } else {
      console.log('Form is invalid:', this.addForm.errors);
      console.log('Form value:', this.addForm.value);
      alert('Please fill in all required fields correctly');
    }
  }

  getStatusBadgeClass(active: boolean): string {
    return active ? 'badge--green' : 'badge--red';
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

  toggleDoctorStatus(doctor: Doctor): void {
    const newStatus = !doctor.active;
    this.adminService.updateDoctor(doctor.doctorId, { active: newStatus }).subscribe({
      next: (response) => {
        if (response.success) {
          doctor.active = newStatus;
          this.loadDoctors();
        }
      },
      error: (error) => {
        console.error('Error updating doctor status:', error);
        this.toastService.showError('Failed to update doctor status: ' + (error.error?.message || error.message || 'Unknown error'));
      }
    });
  }

  getDoctorName(doctor: Doctor): string {
    return `Dr. ${doctor.firstName} ${doctor.lastName}`;
  }



  // Custom validator for doctor email
  doctorEmailValidator(control: any) {
    if (control.value && !control.value.endsWith('@doctor.com')) {
      return { invalidDoctorEmail: true };
    }
    return null;
  }


  onPhoneInput(event: any, controlName: string): void {
    const value = event.target.value;
    const phoneRegex = /^[6-9]\d{0,9}$/;
    if (!phoneRegex.test(value)) {
      event.target.value = value.replace(/[^6-9]/, '').substring(0, 10);
      this.addForm.get(controlName)?.setValue(event.target.value);
    }
  }

  // Error message helpers
  getErrorMessage(controlName: string, form: FormGroup): string {
    const control = form.get(controlName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) return `${this.getFieldLabel(controlName)} is required`;
      if (control.errors['invalidText']) return `${this.getFieldLabel(controlName)} should contain only letters and spaces`;
      if (control.errors['invalidPhone']) return 'Phone number should start with 6-9 and have 10 digits';
      if (control.errors['invalidEmail']) return 'Please enter a valid email address';
      if (control.errors['invalidDoctorEmail']) return 'Doctor email must end with @doctor.com';
      if (control.errors['numbersOnly']) return `${this.getFieldLabel(controlName)} should contain only numbers`;
      if (control.errors['pastDate']) return 'Please select a past date';
      if (control.errors['futureDate']) return 'Please select a future date';
      if (control.errors['tooOld']) return `Age cannot be more than ${control.errors['tooOld'].maxAge} years`;
      if (control.errors['beforeMinDate']) return `Joining date cannot be before ${control.errors['beforeMinDate'].minYear}`;
      if (control.errors['experienceLessThanJoining']) {
        const error = control.errors['experienceLessThanJoining'];
        return `Years of experience (${error.yearsOfExp}) cannot be less than years since joining date (${error.yearsSinceJoining})`;
      }
      if (control.errors['minlength']) return `${this.getFieldLabel(controlName)} should be at least ${control.errors['minlength'].requiredLength} characters`;
      if (control.errors['maxlength']) return `${this.getFieldLabel(controlName)} should not exceed ${control.errors['maxlength'].requiredLength} characters`;
      if (control.errors['min']) return `${this.getFieldLabel(controlName)} should be at least ${control.errors['min'].min}`;
      if (control.errors['max']) return `${this.getFieldLabel(controlName)} should not exceed ${control.errors['max'].max}`;
      if (control.errors['invalidLicenseNumber']) return 'License number should be 2-5 letters followed by 3-6 digits (e.g., CARD001, NEURO002)';
      if (control.errors['invalidConsultationFee']) return 'Please enter a valid consultation fee between 0 and 10000';
      // Password validation errors
      if (control.errors['minLength']) return 'Password must be at least 8 characters long';
      if (control.errors['noUppercase']) return 'Password must contain at least one uppercase letter';
      if (control.errors['noLowercase']) return 'Password must contain at least one lowercase letter';
      if (control.errors['noDigit']) return 'Password must contain at least one number';
      if (control.errors['noSpecialChar']) return 'Password must contain at least one special character';
    }
    return '';
  }

  getFieldLabel(controlName: string): string {
    const labels: { [key: string]: string } = {
      'firstName': 'First Name',
      'lastName': 'Last Name',
      'email': 'Email',
      'password': 'Password',
      'contact': 'Contact Number',
      'emergencyContactName': 'Emergency Contact Name',
      'emergencyContactNum': 'Emergency Contact Number',
      'state': 'State',
      'city': 'City',
      'country': 'Country',
      'postalCode': 'Postal Code',
      'qualification': 'Qualification',
      'licenseNumber': 'License Number',
      'consultationFee': 'Consultation Fee',
      'yearsOfExp': 'Years of Experience',
      'joiningDate': 'Joining Date',
      'slotStartTime': 'Slot Start Time',
      'slotEndTime': 'Slot End Time',
      'appointmentDuration': 'Appointment Duration',
      'workingDays': 'Working Days'
    };
    return labels[controlName] || controlName;
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

  onLicenseNumberInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const licenseValue = value.replace(/[^A-Z0-9]/g, '').toUpperCase();
    if (target.value !== licenseValue) {
      target.value = licenseValue;
      this.addForm.get('licenseNumber')?.setValue(licenseValue);
      this.editForm.get('licenseNumber')?.setValue(licenseValue);
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

  // Get maximum date for joining date (today)
  getMaxDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  // Check if form has been modified
  isFormModified(): boolean {
    if (!this.selectedDoctor) return false;
    
    const currentValues = this.editForm.value;
    const originalValues = {
      firstName: this.selectedDoctor.firstName,
      lastName: this.selectedDoctor.lastName,
      contact: this.selectedDoctor.contact || '',
      gender: this.selectedDoctor.gender || '',
      emergencyContactName: this.selectedDoctor.emergencyContactName || '',
      emergencyContactNum: this.selectedDoctor.emergencyContactNum || '',
      state: this.selectedDoctor.state || '',
      city: this.selectedDoctor.city || '',
      address: this.selectedDoctor.address || '',
      country: this.selectedDoctor.country || '',
      countryCode: this.selectedDoctor.countryCode || '+91',
      postalCode: this.selectedDoctor.postalCode || '',
      bloodGroup: this.selectedDoctor.bloodGroup || '',
      profileUrl: this.selectedDoctor.profileUrl || '',
      specializationId: this.selectedDoctor.specialization.specializationId,
      licenseNumber: this.selectedDoctor.licenseNumber,
      qualification: this.selectedDoctor.qualification,
      bio: this.selectedDoctor.bio || '',
      consultationFee: this.selectedDoctor.consultationFee?.toString() || '',
      yearsOfExp: this.selectedDoctor.yearsOfExp,
      active: this.selectedDoctor.active,
      joiningDate: this.selectedDoctor.joiningDate,
      slotStartTime: this.selectedDoctor.slotStartTime || '09:00',
      slotEndTime: this.selectedDoctor.slotEndTime || '17:00',
      appointmentDuration: this.selectedDoctor.appointmentDuration || 30,
      workingDays: this.selectedDoctor.workingDays || 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY'
    };

    return JSON.stringify(currentValues) !== JSON.stringify(originalValues);
  }

  // Working days helper methods for add form
  isWorkingDaySelectedAdd(day: string): boolean {
    const workingDays = this.addForm.get('workingDays')?.value || '';
    if (!workingDays) return false;
    return workingDays.split(',').includes(day);
  }

  updateWorkingDays(event: any, day: string): void {
    const isChecked = event.target.checked;
    const currentDays = this.addForm.get('workingDays')?.value || '';
    let newDays: string[] = [];
    
    if (currentDays) {
      newDays = currentDays.split(',').filter((d: string) => d.trim() !== '');
    }
    
    if (isChecked) {
      if (!newDays.includes(day)) {
        newDays.push(day);
      }
    } else {
      newDays = newDays.filter((d: string) => d !== day);
    }
    
    this.addForm.get('workingDays')?.setValue(newDays.join(','));
    this.addForm.get('workingDays')?.markAsTouched();
  }

  closeAddModal(): void {
    this.showAddModal = false;
    
    // Use setTimeout to ensure the form is properly reset
    setTimeout(() => {
      // Reset form and immediately set default values
      this.addForm.reset({
        consultationFee: 100,
        active: true,
        slotStartTime: '09:00',
        slotEndTime: '17:00',
        appointmentDuration: 30,
        workingDays: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY'
      });
    }, 0);
  }
}
