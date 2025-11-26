import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  
  // Date validators
  static futureOrToday(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate < today) {
      return { pastDate: true };
    }
    return null;
  }

  static pastDate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    // Set to start of today for more accurate comparison
    today.setHours(0, 0, 0, 0);
    selectedDate.setHours(0, 0, 0, 0);
    
    if (selectedDate > today) {
      return { pastDate: true };
    }
    return null;
  }

  static maxAge(maxYears: number = 100): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    const maxDate = new Date();
    maxDate.setFullYear(today.getFullYear() - maxYears);
    
    if (selectedDate < maxDate) {
      return { tooOld: { maxAge: maxYears } };
    }
    return null;
    };
  }

  // Minimum date validator (e.g., not before 1960)
  static minDate(minYear: number = 1960): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      
      const selectedDate = new Date(control.value);
      const minDate = new Date(minYear, 0, 1); // January 1st of minYear
      
      if (selectedDate < minDate) {
        return { beforeMinDate: { minYear } };
      }
      return null;
    };
  }

  // Experience vs joining date validator (corrected logic)
  static experienceVsJoiningDate(experienceControlName: string = 'yearsOfExp'): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      
      const joiningDate = new Date(control.value);
      const today = new Date();
      const yearsSinceJoining = Math.floor((today.getTime() - joiningDate.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
      
      // Get the experience control from the parent form
      const parent = control.parent;
      if (!parent) return null;
      
      const experienceControl = parent.get(experienceControlName);
      if (!experienceControl || !experienceControl.value) return null;
      
      const yearsOfExp = parseInt(experienceControl.value);
      
      // CORRECTED LOGIC: Experience should be >= years since joining
      // A doctor can have more experience than years at this hospital
      // Debug logging
      console.log('Experience validation:', {
        yearsOfExp,
        yearsSinceJoining,
        joiningDate: joiningDate.toISOString().split('T')[0],
        isValid: yearsOfExp >= yearsSinceJoining
      });
      
      if (yearsOfExp < yearsSinceJoining) {
        return { experienceLessThanJoining: { 
          yearsOfExp, 
          yearsSinceJoining,
          joiningDate: joiningDate.toISOString().split('T')[0]
        } };
      }
      
      return null;
    };
  }

  // Reverse validator for experience field (corrected logic)
  static experienceValidForJoiningDate(joiningDateControlName: string = 'joiningDate'): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // Get the joining date control from the parent form
      const parent = control.parent;
      if (!parent) return null;
      
      const joiningDateControl = parent.get(joiningDateControlName);
      if (!joiningDateControl || !joiningDateControl.value) return null;
      
      const joiningDate = new Date(joiningDateControl.value);
      const today = new Date();
      const yearsSinceJoining = Math.floor((today.getTime() - joiningDate.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
      
      // If no experience value provided, but we have a joining date, show validation
      if (control.value === null || control.value === undefined || control.value === '') {
        if (yearsSinceJoining > 0) {
          return { experienceLessThanJoining: { 
            yearsOfExp: 0, 
            yearsSinceJoining,
            joiningDate: joiningDate.toISOString().split('T')[0]
          } };
        }
        return null;
      }
      
      const yearsOfExp = parseInt(control.value);
      if (isNaN(yearsOfExp)) return null;
      
      // CORRECTED LOGIC: Experience should be >= years since joining
      // Debug logging
      console.log('Experience validation (reverse):', {
        yearsOfExp,
        yearsSinceJoining,
        joiningDate: joiningDate.toISOString().split('T')[0],
        isValid: yearsOfExp >= yearsSinceJoining
      });
      
      if (yearsOfExp < yearsSinceJoining) {
        return { experienceLessThanJoining: { 
          yearsOfExp, 
          yearsSinceJoining,
          joiningDate: joiningDate.toISOString().split('T')[0]
        } };
      }
      
      return null;
    };
  }

  // Contact number validators
  static indianPhoneNumber(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const phoneRegex = /^[6-9]\d{9}$/;
    if (!phoneRegex.test(control.value)) {
      return { invalidPhone: true };
    }
    return null;
  }

  // Name validators
  static textOnly(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const textRegex = /^[a-zA-Z\s]+$/;
    if (!textRegex.test(control.value)) {
      return { invalidText: true };
    }
    return null;
  }

  // Email validators
  static email(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const email = control.value.toLowerCase();
    const emailRegex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/;
    if (!emailRegex.test(email)) {
      return { invalidEmail: true };
    }
    return null;
  }

  // Doctor email validators
  static doctorEmail(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const emailRegex = /^[^\s@]+@doctor\.com$/;
    if (!emailRegex.test(control.value)) {
      return { invalidDoctorEmail: true };
    }
    return null;
  }

  // Input restrictions
  static numbersOnly(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const numberRegex = /^\d+$/;
    if (!numberRegex.test(control.value)) {
      return { numbersOnly: true };
    }
    return null;
  }

  static textOnlyInput(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const textRegex = /^[a-zA-Z\s]*$/;
    if (!textRegex.test(control.value)) {
      return { textOnly: true };
    }
    return null;
  }

  // Postal code validators
  static postalCode(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const postalRegex = /^\d{6}$/;
    if (!postalRegex.test(control.value)) {
      return { invalidPostalCode: true };
    }
    return null;
  }

  // Password strength validator
  static passwordStrength(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const password = control.value;
    const errors: ValidationErrors = {};
    
    if (password.length < 8) {
      errors['minLength'] = true;
    }
    if (!/[A-Z]/.test(password)) {
      errors['noUppercase'] = true;
    }
    if (!/[a-z]/.test(password)) {
      errors['noLowercase'] = true;
    }
    if (!/[0-9]/.test(password)) {
      errors['noDigit'] = true;
    }
    if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
      errors['noSpecialChar'] = true;
    }
    
    return Object.keys(errors).length > 0 ? errors : null;
  }

  // Username validator
  static username(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const usernameRegex = /^[a-zA-Z0-9._-]{3,20}$/;
    if (!usernameRegex.test(control.value)) {
      return { invalidUsername: true };
    }
    return null;
  }

  // Blood group validator
  static bloodGroup(control: AbstractControl): ValidationErrors | null {
    if (!control.value || control.value === '') return null;
    
    const validBloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
    if (!validBloodGroups.includes(control.value)) {
      return { invalidBloodGroup: true };
    }
    return null;
  }

  // License number validator
  static licenseNumber(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    // Allow 2-5 letters followed by 3-6 digits (e.g., ORTHO002, CARD001, NEURO123)
    const licenseRegex = /^[A-Z]{2,5}\d{3,6}$/;
    if (!licenseRegex.test(control.value)) {
      return { invalidLicenseNumber: true };
    }
    return null;
  }

  // Consultation fee validator
  static consultationFee(control: AbstractControl): ValidationErrors | null {
    if (!control.value || control.value === '') return null;
    
    // Check if it's a valid number
    const fee = parseFloat(control.value);
    if (isNaN(fee)) {
      return { invalidConsultationFee: true };
    }
    
    // Check if it's a positive number
    if (fee <= 0) {
      return { invalidConsultationFee: true };
    }
    
    // Check if it's within reasonable range (₹1 to ₹50,000)
    if (fee > 50000) {
      return { invalidConsultationFee: true };
    }
    
    return null;
  }
}
