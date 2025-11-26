import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;
  showConfirmPassword = false;
  formSubmitted = false;
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

  genders = [
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
    { value: 'OTHER', label: 'Other' }
  ];

  bloodGroups = [
    { value: '', label: 'Select Blood Group (Optional)' },
    { value: 'A+', label: 'A+' },
    { value: 'A-', label: 'A-' },
    { value: 'B+', label: 'B+' },
    { value: 'B-', label: 'B-' },
    { value: 'AB+', label: 'AB+' },
    { value: 'AB-', label: 'AB-' },
    { value: 'O+', label: 'O+' },
    { value: 'O-', label: 'O-' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      // Personal Information
      firstName: ['', [Validators.required, Validators.minLength(2), this.textOnlyValidator]],
      lastName: ['', [Validators.required, Validators.minLength(2), this.textOnlyValidator]],
      email: ['', [Validators.required, this.customEmailValidator]],
      phoneNumber: ['', [Validators.required, this.phoneNumberValidator]],
      countryCode: ['+91', [Validators.required]],
      dateOfBirth: ['', [Validators.required, this.dateRangeValidator]],
      gender: ['', [Validators.required]],
      bloodGroup: [''], // Optional field
      password: ['', [Validators.required, Validators.minLength(8), this.passwordStrengthValidator]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit() {
    // Check if user is already logged in
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/home']);
    }
  }

  onSubmit() {
    this.formSubmitted = true;
    
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const formData = this.registerForm.value;
      const registrationData = {
        name: `${formData.firstName} ${formData.lastName}`,
        firstname: formData.firstName,
        lastname: formData.lastName,
        email: formData.email,
        contact: formData.phoneNumber,
        countryCode: formData.countryCode,
        address: 'Default Address', // Will be updated when we add address field
        username: formData.email.split('@')[0],
        password: formData.password,
        confirmPassword: formData.confirmPassword,
        gender: formData.gender,
        bloodGroup: formData.bloodGroup || null,
        birthdate: formData.dateOfBirth, // Fixed: using correct field name
        role: 'PATIENT' // Always set to PATIENT
      };
      
      this.authService.register(registrationData).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.snackBar.open('Registration successful! Please login to continue.', 'Close', {
              duration: 5000,
              panelClass: ['success-snackbar']
            });
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = response.message || 'Registration failed';
            this.snackBar.open(this.errorMessage, 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'An error occurred during registration';
          this.snackBar.open(this.errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    } else {
      this.markFormGroupTouched();
      this.snackBar.open('Please fix the form errors and try again.', 'Close', {
        duration: 3000,
        panelClass: ['warning-snackbar']
      });
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  // Prevent non-text characters in name fields
  onNameKeyPress(event: KeyboardEvent): void {
    const pattern = /^[a-zA-Z\s]*$/;
    const inputChar = String.fromCharCode(event.charCode);
    if (!pattern.test(inputChar) && event.charCode !== 0) {
      event.preventDefault();
    }
  }

  // Enhanced phone number input handler
  onPhoneKeyPress(event: KeyboardEvent): void {
    const pattern = /^[0-9]*$/;
    const inputChar = String.fromCharCode(event.charCode);
    if (!pattern.test(inputChar) && event.charCode !== 0) {
      event.preventDefault();
    }
  }

  // Email input handler to prevent uppercase letters
  onEmailInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const inputValue = target.value;
    const lowerCaseValue = inputValue.toLowerCase();
    
    if (inputValue !== lowerCaseValue) {
      target.value = lowerCaseValue;
      this.registerForm.get('email')?.setValue(lowerCaseValue);
      this.registerForm.get('email')?.updateValueAndValidity();
    }
  }

  // Live validation for email uppercase - auto convert to lowercase
  onEmailKeyPress(event: KeyboardEvent): void {
    const inputChar = String.fromCharCode(event.charCode);
    if (/[A-Z]/.test(inputChar)) {
      event.preventDefault();
      // Auto-convert to lowercase instead of showing error
      const target = event.target as HTMLInputElement;
      const currentValue = target.value;
      const newValue = currentValue + inputChar.toLowerCase();
      target.value = newValue;
      this.registerForm.get('email')?.setValue(newValue);
      this.registerForm.get('email')?.updateValueAndValidity();
    }
  }

  private showTempErrorMessage(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 2000,
      panelClass: ['warning-snackbar']
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  // Custom validator for text-only fields (names)
  textOnlyValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    const pattern = /^[a-zA-Z\s]+$/;
    return pattern.test(control.value) ? null : { textOnly: true };
  }

  // Enhanced phone number validator
  phoneNumberValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    
    const phoneNumber = control.value.toString();
    
    // Check if it contains only digits
    if (!/^[0-9]+$/.test(phoneNumber)) {
      return { digitOnly: true };
    }
    
    // Check exact length (must be 10 digits)
    if (phoneNumber.length !== 10) {
      return { invalidLength: true };
    }
    
    // Check if first digit is between 6-9
    const firstDigit = parseInt(phoneNumber.charAt(0));
    if (firstDigit < 6 || firstDigit > 9) {
      return { invalidFirstDigit: true };
    }
    
    return null;
  }

  // Simplified email validator (removed length validation)
  customEmailValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    
    const email = control.value.toLowerCase();
    
    // Check if email contains uppercase (for live validation)
    if (control.value !== email) {
      return { hasUppercase: true };
    }
    
    // Basic email pattern validation
    const emailPattern = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/;
    const isValid = emailPattern.test(email);
    
    if (!isValid) {
      return { invalidEmail: true };
    }
    
    return null;
  }

  // Date range validator (must be between today and 100 years ago)
  dateRangeValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    const hundredYearsAgo = new Date();
    hundredYearsAgo.setFullYear(today.getFullYear() - 100);
    
    // Reset time to start of day for accurate comparison
    today.setHours(0, 0, 0, 0);
    selectedDate.setHours(0, 0, 0, 0);
    hundredYearsAgo.setHours(0, 0, 0, 0);
    
    if (selectedDate >= today) {
      return { futureDate: true };
    }
    
    if (selectedDate < hundredYearsAgo) {
      return { tooOld: true };
    }
    
    return null;
  }

  // Strong password validator
  passwordStrengthValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    
    const password = control.value;
    const errors: ValidationErrors = {};
    
    // Check for uppercase letter
    if (!/[A-Z]/.test(password)) {
      errors['noUppercase'] = true;
    }
    
    // Check for lowercase letter
    if (!/[a-z]/.test(password)) {
      errors['noLowercase'] = true;
    }
    
    // Check for digit
    if (!/[0-9]/.test(password)) {
      errors['noDigit'] = true;
    }
    
    // Check for special character
    if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
      errors['noSpecialChar'] = true;
    }
    
    return Object.keys(errors).length > 0 ? errors : null;
  }

  // Password match validator
  passwordMatchValidator = (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ mismatch: true });
      return { mismatch: true };
    } else if (confirmPassword && confirmPassword.errors && confirmPassword.errors['mismatch']) {
      delete confirmPassword.errors['mismatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  // Helper methods for password validation display
  hasMinLength(password: string): boolean {
    if (!password) return false;
    return password.length >= 8;
  }

  hasUppercase(password: string): boolean {
    if (!password) return false;
    return /[A-Z]/.test(password);
  }

  hasLowercase(password: string): boolean {
    if (!password) return false;
    return /[a-z]/.test(password);
  }

  hasDigit(password: string): boolean {
    if (!password) return false;
    return /[0-9]/.test(password);
  }

  hasSpecialChar(password: string): boolean {
    if (!password) return false;
    return /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);
  }
}