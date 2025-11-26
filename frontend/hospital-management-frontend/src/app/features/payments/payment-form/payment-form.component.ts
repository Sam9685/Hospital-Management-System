import { Component, OnInit, ElementRef, QueryList, ViewChildren } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AdminService } from '../../../core/services/admin.service';
import { ToastService } from '../../../core/services/toast.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-payment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="payment-form-container">
      <div class="payment-header">
        <div class="back-button" (click)="goBack()">
          <svg class="back-icon" viewBox="0 0 24 24">
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
          </svg>
          Back to Payment Methods
        </div>
        <h1 class="page-title">Payment Details</h1>
        <p class="page-subtitle">Complete your payment to confirm your appointment</p>
      </div>

      <div class="payment-summary">
        <div class="summary-card">
          <div class="summary-header">
            <h3>Payment Summary</h3>
            <div class="payment-method-badge" [ngClass]="'method-' + selectedMethod.toLowerCase()">
              {{ selectedMethod }}
            </div>
          </div>
          <div class="summary-details">
            <div class="summary-item">
              <span class="label">Doctor:</span>
              <span class="value">{{ appointmentData?.doctorName }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Date & Time:</span>
              <span class="value">{{ formatDate(appointmentData?.appointmentDate) }} at {{ formatTime(appointmentData?.appointmentTime) }}</span>
            </div>
            <div class="summary-item total">
              <span class="label">Amount to Pay:</span>
              <span class="value">₹{{ appointmentData?.consultationFee }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="payment-form-section">
        <form [formGroup]="paymentForm" (ngSubmit)="processPayment()">
          <!-- UPI Payment Form -->
          <div *ngIf="selectedMethod === 'UPI'" class="payment-form upi-form">
            <h3>UPI Payment Details</h3>
            
            <!-- OLD UPI Input (Commented out for reference) -->
            <!--
            <div class="form-group">
              <label class="form-label">UPI ID <span class="required">*</span></label>
              <input type="text" 
                     formControlName="upiId" 
                     class="form-input" 
                     [class.error]="paymentForm.get('upiId')?.invalid && paymentForm.get('upiId')?.touched"
                     placeholder="Enter your UPI ID (e.g., yourname@paytm)"
                     (input)="onUpiIdInput($event)">
              <div *ngIf="paymentForm.get('upiId')?.invalid && paymentForm.get('upiId')?.touched" 
                   class="form-error">
                <div *ngIf="paymentForm.get('upiId')?.errors?.['required']">
                  UPI ID is required
                </div>
                <div *ngIf="paymentForm.get('upiId')?.errors?.['invalidUpiFormat']">
                  Please enter a valid UPI ID (e.g., name&#64;bank or mobile&#64;bank)
                </div>
              </div>
            </div>
            -->
            
            <!-- NEW Improved UPI Input with Better UX -->
            <div class="form-group">
              <label class="form-label">UPI ID <span class="required">*</span></label>
              <div class="upi-input-container">
                <input type="text" 
                       formControlName="upiUsername" 
                       class="upi-username-input" 
                       [class.error]="paymentForm.get('upiUsername')?.invalid && paymentForm.get('upiUsername')?.touched"
                       placeholder="yourname"
                       (input)="onUpiUsernameInput($event)"
                       maxlength="20">
                <span class="upi-at-symbol">&#64;</span>
                <select formControlName="upiBank" 
                        class="upi-bank-select"
                        [class.error]="paymentForm.get('upiBank')?.invalid && paymentForm.get('upiBank')?.touched">
                  <option value="">Select Bank</option>
                  <option *ngFor="let bank of upiBanks" [value]="bank.value">
                    {{ bank.label }}
                  </option>
                </select>
              </div>
              <div *ngIf="(paymentForm.get('upiUsername')?.invalid || paymentForm.get('upiBank')?.invalid) && (paymentForm.get('upiUsername')?.touched || paymentForm.get('upiBank')?.touched)" 
                   class="form-error">
                <div *ngIf="paymentForm.get('upiUsername')?.errors?.['required']">
                  UPI username is required
                </div>
                <div *ngIf="paymentForm.get('upiUsername')?.errors?.['invalidUpiUsername']">
                  Username should contain only letters, numbers, dots, and underscores
                </div>
                <div *ngIf="paymentForm.get('upiBank')?.errors?.['required']">
                  Please select a bank
                </div>
              </div>
              <small class="form-hint">Complete UPI ID: {{ getCompleteUpiId() }}</small>
            </div>
            
            <div class="form-group">
              <label class="form-label">Mobile Number <span class="required">*</span></label>
              <input type="tel" 
                     formControlName="mobileNumber" 
                     class="form-input" 
                     [class.error]="paymentForm.get('mobileNumber')?.invalid && paymentForm.get('mobileNumber')?.touched"
                     placeholder="Enter your 10-digit mobile number"
                     maxlength="10"
                     (input)="onMobileInput($event)">
              <div *ngIf="paymentForm.get('mobileNumber')?.invalid && paymentForm.get('mobileNumber')?.touched" 
                   class="form-error">
                <div *ngIf="paymentForm.get('mobileNumber')?.errors?.['required']">
                  Mobile number is required
                </div>
                <div *ngIf="paymentForm.get('mobileNumber')?.errors?.['minlength'] || paymentForm.get('mobileNumber')?.errors?.['maxlength']">
                  Mobile number must be exactly 10 digits
                </div>
                <div *ngIf="paymentForm.get('mobileNumber')?.errors?.['invalidMobileFormat']">
                  Mobile number must start with 6, 7, 8, or 9
                </div>
              </div>
            </div>
          </div>

          <!-- Card Payment Form -->
          <div *ngIf="selectedMethod === 'CARD'" class="payment-form card-form">
            <h3>Card Payment Details</h3>
            <div class="form-group">
              <label class="form-label">Cardholder Name <span class="required">*</span></label>
              <input type="text" 
                     formControlName="cardholderName" 
                     class="form-input" 
                     [class.error]="paymentForm.get('cardholderName')?.invalid && paymentForm.get('cardholderName')?.touched"
                     placeholder="Enter cardholder name as on card"
                     maxlength="30"
                     (input)="onCardholderNameInput($event)"
                     (keypress)="onCardholderNameKeyPress($event)">
              <div *ngIf="paymentForm.get('cardholderName')?.invalid && paymentForm.get('cardholderName')?.touched" 
                   class="form-error">
                <div *ngIf="paymentForm.get('cardholderName')?.errors?.['required']">
                  Cardholder name is required
                </div>
                <div *ngIf="paymentForm.get('cardholderName')?.errors?.['minlength']">
                  Name must be at least 2 characters long
                </div>
                <div *ngIf="paymentForm.get('cardholderName')?.errors?.['maxlength']">
                  Name cannot exceed 30 characters
                </div>
                <div *ngIf="paymentForm.get('cardholderName')?.errors?.['pattern']">
                  Name should contain only letters and spaces
                </div>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Card Number <span class="required">*</span></label>
              <input type="text" 
                     formControlName="cardNumber" 
                     class="form-input" 
                     [class.error]="paymentForm.get('cardNumber')?.invalid && paymentForm.get('cardNumber')?.touched"
                     placeholder="1234 5678 9012 3456" 
                     maxlength="19"
                     (input)="onCardNumberInput($event)">
              <div *ngIf="paymentForm.get('cardNumber')?.invalid && paymentForm.get('cardNumber')?.touched" 
                   class="form-error">
                <div *ngIf="paymentForm.get('cardNumber')?.errors?.['required']">
                  Card number is required
                </div>
                <div *ngIf="paymentForm.get('cardNumber')?.errors?.['minlength'] || paymentForm.get('cardNumber')?.errors?.['maxlength']">
                  Card number must be exactly 16 digits
                </div>
                <div *ngIf="paymentForm.get('cardNumber')?.errors?.['pattern']">
                  Please enter a valid card number
                </div>
              </div>
            </div>

            <!-- NEW: Expiry inputs — four small boxed inputs (two for MM as digits, two for YY as digits) -->
            <div class="form-row expiry-row">
              <div class="form-group expiry-group">
                <label class="form-label">Expiry (MM / YY) <span class="required">*</span></label>
                <div class="expiry-inputs">
                  <input
                    #expiryInput
                    type="text"
                    inputmode="numeric"
                    maxlength="1"
                    class="expiry-box"
                    placeholder="M"
                    [class.error]="isExpiryTouchedAndInvalid()"
                    formControlName="expiryM1"
                    (input)="onExpiryInput($event, 0)"
                    (keydown)="onExpiryKeydown($event, 0)"
                  />
                  <input
                    #expiryInput
                    type="text"
                    inputmode="numeric"
                    maxlength="1"
                    class="expiry-box"
                    placeholder="M"
                    [class.error]="isExpiryTouchedAndInvalid()"
                    formControlName="expiryM2"
                    (input)="onExpiryInput($event, 1)"
                    (keydown)="onExpiryKeydown($event, 1)"
                  />
                  <div class="expiry-sep">/</div>
                  <input
                    #expiryInput
                    type="text"
                    inputmode="numeric"
                    maxlength="1"
                    class="expiry-box"
                    placeholder="Y"
                    [class.error]="isExpiryTouchedAndInvalid()"
                    formControlName="expiryY1"
                    (input)="onExpiryInput($event, 2)"
                    (keydown)="onExpiryKeydown($event, 2)"
                  />
                  <input
                    #expiryInput
                    type="text"
                    inputmode="numeric"
                    maxlength="1"
                    class="expiry-box"
                    placeholder="Y"
                    [class.error]="isExpiryTouchedAndInvalid()"
                    formControlName="expiryY2"
                    (input)="onExpiryInput($event, 3)"
                    (keydown)="onExpiryKeydown($event, 3)"
                  />
                </div>

                <div *ngIf="isExpiryTouchedAndInvalid()" class="form-error expiry-error">
                  <div *ngIf="paymentForm.errors?.['expiredDate']">
                    Card expiry cannot be in the past.
                  </div>
                  <div *ngIf="paymentForm.errors?.['invalidExpiryFormat']">
                    Please enter a valid expiry month (01-12) and year.
                  </div>
                </div>
              </div>

              <div class="form-group cvv-group">
                <label class="form-label">CVV <span class="required">*</span></label>
                <input type="text" 
                       formControlName="cvv" 
                       class="form-input" 
                       [class.error]="paymentForm.get('cvv')?.invalid && paymentForm.get('cvv')?.touched"
                       placeholder="123" 
                       maxlength="3"
                       (input)="onCvvInput($event)">
                <div *ngIf="paymentForm.get('cvv')?.invalid && paymentForm.get('cvv')?.touched" 
                     class="form-error">
                  <div *ngIf="paymentForm.get('cvv')?.errors?.['required']">
                    CVV is required
                  </div>
                  <div *ngIf="paymentForm.get('cvv')?.errors?.['minlength'] || paymentForm.get('cvv')?.errors?.['maxlength']">
                    CVV must be exactly 3 digits
                  </div>
                  <div *ngIf="paymentForm.get('cvv')?.errors?.['pattern']">
                    CVV should contain only numbers
                  </div>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Billing Address <span class="required">*</span></label>
              <textarea formControlName="billingAddress" 
                        class="form-textarea" 
                        [class.error]="paymentForm.get('billingAddress')?.invalid && paymentForm.get('billingAddress')?.touched"
                        placeholder="Enter your billing address" 
                        rows="3"></textarea>
              <div *ngIf="paymentForm.get('billingAddress')?.invalid && paymentForm.get('billingAddress')?.touched" 
                   class="form-error">
                <div *ngIf="paymentForm.get('billingAddress')?.errors?.['required']">
                  Billing address is required
                </div>
                <div *ngIf="paymentForm.get('billingAddress')?.errors?.['minlength']">
                  Address must be at least 10 characters long
                </div>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="goBack()">
              Back
            </button>
            <button type="submit" class="btn btn-primary" 
                    [disabled]="!paymentForm.valid || isProcessing">
              <svg *ngIf="isProcessing" class="loading-spinner" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                  <animate attributeName="stroke-dasharray" dur="2s" values="0 31.416;15.708 15.708;0 31.416" repeatCount="indefinite"/>
                  <animate attributeName="stroke-dashoffset" dur="2s" values="0;-15.708;-31.416" repeatCount="indefinite"/>
                </circle>
              </svg>
              <span *ngIf="!isProcessing">Pay ₹{{ appointmentData?.consultationFee }}</span>
              <span *ngIf="isProcessing">Processing Payment...</span>
            </button>
          </div>
        </form>
      </div>

      <div class="security-notice">
        <div class="security-icon">
          <svg viewBox="0 0 24 24">
            <path d="M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M12,7C13.4,7 14.8,8.6 14.8,10V11H16V16H8V11H9.2V10C9.2,8.6 10.6,7 12,7M12,8.2C11.2,8.2 10.4,8.7 10.4,10V11H13.6V10C13.6,8.7 12.8,8.2 12,8.2Z"/>
          </svg>
        </div>
        <div class="security-text">
          <h4>Secure Payment</h4>
          <p>Your payment information is encrypted and secure. We use industry-standard security measures to protect your data.</p>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./payment-form.component.css']
})
export class PaymentFormComponent implements OnInit {
  paymentForm: FormGroup;
  selectedMethod: string = '';
  appointmentData: any = null;
  currentUser: User | null = null;
  isProcessing = false;
  currentDate = new Date();

  // UPI Banks list
  upiBanks = [
    { value: 'paytm', label: 'Paytm' },
    { value: 'phonepe', label: 'PhonePe' },
    { value: 'gpay', label: 'Google Pay' },
    { value: 'bhim', label: 'BHIM' },
    { value: 'amazonpay', label: 'Amazon Pay' },
    { value: 'mobikwik', label: 'MobiKwik' },
    { value: 'freecharge', label: 'FreeCharge' },
    { value: 'jupiter', label: 'Jupiter' },
    { value: 'cred', label: 'CRED' },
    { value: 'whatsapp', label: 'WhatsApp Pay' },
    { value: 'yono', label: 'YONO (SBI)' },
    { value: 'icici', label: 'iMobile (ICICI)' },
    { value: 'hdfc', label: 'HDFC PayZapp' },
    { value: 'axis', label: 'Axis Pay' },
    { value: 'kotak', label: 'Kotak 811' },
    { value: 'pnb', label: 'PNB One' },
    { value: 'bob', label: 'BOB UPI' },
    { value: 'canara', label: 'Canara Bank' },
    { value: 'union', label: 'Union Bank' },
    { value: 'indian', label: 'Indian Bank' }
  ];

  // To manage auto-focus of the 4 expiry boxes
  @ViewChildren('expiryInput') expiryInputs!: QueryList<ElementRef>;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private adminService: AdminService,
    private toastService: ToastService
  ) {
    this.paymentForm = this.fb.group({});
  }

  ngOnInit(): void {
    this.loadData();
    this.initializeForm();
  }

  loadData(): void {
    this.selectedMethod = sessionStorage.getItem('selectedPaymentMethod') || '';
    const appointmentData = sessionStorage.getItem('pendingAppointment');

    if (appointmentData) {
      this.appointmentData = JSON.parse(appointmentData);
    } else {
      this.router.navigate(['/appointments/schedule']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.selectedMethod) {
      this.router.navigate(['/payments/select']);
    }
  }

  onNameKeyPress(event: KeyboardEvent): void {
    const pattern = /^[a-zA-Z\s]*$/;
    const inputChar = String.fromCharCode(event.charCode);
    if (!pattern.test(inputChar) && event.charCode !== 0) {
      event.preventDefault();
    }
  }

  onCardholderNameInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let value = target.value;
    
    // Remove any non-letter and non-space characters
    value = value.replace(/[^a-zA-Z\s]/g, '');
    
    // Convert to uppercase
    value = value.toUpperCase();
    
    // Limit to 30 characters
    if (value.length > 30) {
      value = value.substring(0, 30);
    }
    
    // Update the input value and form control
    target.value = value;
    this.paymentForm.get('cardholderName')?.setValue(value);
  }

  onCardholderNameKeyPress(event: KeyboardEvent): void {
    const pattern = /^[a-zA-Z\s]*$/;
    const inputChar = String.fromCharCode(event.charCode);
    if (!pattern.test(inputChar) && event.charCode !== 0) {
      event.preventDefault();
    }
  }

  initializeForm(): void {
    if (this.selectedMethod === 'UPI') {
      this.paymentForm = this.fb.group({
        // OLD UPI field (commented out for reference)
        // upiId: ['', [Validators.required, this.upiValidator]],
        
        // NEW UPI fields
        upiUsername: ['', [Validators.required, this.upiUsernameValidator]],
        upiBank: ['', [Validators.required]],
        mobileNumber: ['', [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(10),
          this.mobileNumberValidator
        ]]
      });
    } else if (this.selectedMethod === 'CARD') {
      this.paymentForm = this.fb.group({
        cardholderName: ['', [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(30),
          Validators.pattern(/^[a-zA-Z\s]+$/)
        ]],
        cardNumber: ['', [
          Validators.required,
          Validators.minLength(16),
          Validators.maxLength(16),
          Validators.pattern(/^\d{16}$/)
        ]],
        // Replaced select-based expiry with 4 single-digit boxes: expiryM1, expiryM2, expiryY1, expiryY2
        expiryM1: ['', [Validators.required, Validators.pattern(/^\d$/)]],
        expiryM2: ['', [Validators.required, Validators.pattern(/^\d$/)]],
        expiryY1: ['', [Validators.required, Validators.pattern(/^\d$/)]],
        expiryY2: ['', [Validators.required, Validators.pattern(/^\d$/)]],

        cvv: ['', [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(3),
          Validators.pattern(/^\d{3}$/)
        ]],
        billingAddress: ['', [Validators.required, Validators.minLength(10)]]
      });

      // Add validator to check if expiry date is not in the past (uses the 4 expiry digit controls)
      this.paymentForm.addValidators(this.expiryDateValidator.bind(this));
    }
  }

  // Custom Validators
  upiValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;

    // UPI ID format: username@bank (e.g., name@paytm, mobile@bank)
    const upiPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$/;

    if (!upiPattern.test(control.value)) {
      return { invalidUpiFormat: true };
    }

    return null;
  }

  // NEW UPI Username Validator
  upiUsernameValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;

    // UPI username format: letters, numbers, dots, underscores only
    const usernamePattern = /^[a-zA-Z0-9._-]+$/;

    if (!usernamePattern.test(control.value)) {
      return { invalidUpiUsername: true };
    }

    return null;
  }

  mobileNumberValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;

    const mobilePattern = /^[6-9]\d{9}$/;

    if (!mobilePattern.test(control.value)) {
      return { invalidMobileFormat: true };
    }

    return null;
  }

  /**
   * expiryDateValidator:
   * - Reads digits from expiryM1/M2 and expiryY1/Y2
   * - Builds MM and YY (two-digit month and two-digit year)
   * - Validates format (01-12 month) and that the expiry is current month or a future month/year
   */
  expiryDateValidator = (group: AbstractControl): ValidationErrors | null => {
    const m1 = group.get('expiryM1')?.value;
    const m2 = group.get('expiryM2')?.value;
    const y1 = group.get('expiryY1')?.value;
    const y2 = group.get('expiryY2')?.value;

    // If any digit missing, don't mark as expired yet — let per-field validators handle required
    if ([m1, m2, y1, y2].some(v => v === null || v === undefined || v === '')) {
      // but we can still check format - leave to field validators
      return null;
    }

    const monthStr = `${m1}${m2}`;
    const yearStr = `${y1}${y2}`;

    // Basic numeric check
    if (!/^\d{2}$/.test(monthStr) || !/^\d{2}$/.test(yearStr)) {
      return { invalidExpiryFormat: true };
    }

    const month = parseInt(monthStr, 10);
    const twoDigitYear = parseInt(yearStr, 10);

    if (month < 1 || month > 12) {
      return { invalidExpiryFormat: true };
    }

    // Convert two-digit year to full year (assume 2000-2099)
    const fullYear = 2000 + twoDigitYear;

    const currentYear = this.currentDate.getFullYear();
    const currentMonth = this.currentDate.getMonth() + 1;

    // If the expiry year is before current year -> expired
    if (fullYear < currentYear) {
      return { expiredDate: true };
    }

    // If same year and month less than current -> expired
    if (fullYear === currentYear && month < currentMonth) {
      return { expiredDate: true };
    }

    return null;
  }

  // Input handlers
  onUpiIdInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let value = target.value;
    // Remove spaces and convert to lowercase
    value = value.replace(/\s/g, '').toLowerCase();
    target.value = value;
    this.paymentForm.get('upiId')?.setValue(value);
  }

  // NEW UPI Username Input Handler
  onUpiUsernameInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let value = target.value;
    // Remove spaces and convert to lowercase
    value = value.replace(/\s/g, '').toLowerCase();
    // Remove any invalid characters
    value = value.replace(/[^a-zA-Z0-9._-]/g, '');
    target.value = value;
    this.paymentForm.get('upiUsername')?.setValue(value);
  }

  // Helper method to get complete UPI ID
  getCompleteUpiId(): string {
    const username = this.paymentForm.get('upiUsername')?.value || '';
    const bank = this.paymentForm.get('upiBank')?.value || '';
    
    if (username && bank) {
      return `${username}@${bank}`;
    } else if (username) {
      return `${username}@`;
    } else {
      return '';
    }
  }

  onMobileInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let value = target.value;
    // Remove non-numeric characters
    value = value.replace(/\D/g, '');
    // Limit to 10 digits
    if (value.length > 10) {
      value = value.substring(0, 10);
    }
    target.value = value;
    this.paymentForm.get('mobileNumber')?.setValue(value);
  }

  onCardNumberInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let value = target.value;
    // Remove non-numeric characters
    value = value.replace(/\D/g, '');
    // Add spaces every 4 digits for display
    value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
    // Limit to 16 digits (19 with spaces)
    if (value.replace(/\s/g, '').length > 16) {
      value = value.substring(0, 19);
    }
    target.value = value;
    // Store without spaces in form control
    this.paymentForm.get('cardNumber')?.setValue(value.replace(/\s/g, ''));
  }

  onCvvInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    let value = target.value;
    // Remove non-numeric characters
    value = value.replace(/\D/g, '');
    // Limit to 3 digits
    if (value.length > 3) {
      value = value.substring(0, 3);
    }
    target.value = value;
    this.paymentForm.get('cvv')?.setValue(value);
  }

  /**
   * Expiry input behaviour:
   * - Accept only digits
   * - Auto-move to next input when a digit is entered
   * - Move back on backspace if empty
   * - After every input change, update the form-level validators
   */
  onExpiryInput(event: Event, index: number): void {
    const target = event.target as HTMLInputElement;
    const raw = target.value || '';
    // allow only digits and single character
    let value = raw.replace(/\D/g, '').slice(0, 1);
    target.value = value;
    const controlNames = ['expiryM1', 'expiryM2', 'expiryY1', 'expiryY2'];
    const controlName = controlNames[index];
    this.paymentForm.get(controlName)?.setValue(value);

    // If value entered and not the last box, focus next
    if (value && index < controlNames.length - 1) {
      const inputs = this.expiryInputs.toArray();
      const next = inputs[index + 1];
      if (next) {
        try { (next.nativeElement as HTMLInputElement).focus(); } catch { }
      }
    }

    // Manually trigger validation for the group
    this.paymentForm.updateValueAndValidity({ onlySelf: false, emitEvent: true });
  }

  onExpiryKeydown(event: KeyboardEvent, index: number): void {
    const key = event.key;
    const controlNames = ['expiryM1', 'expiryM2', 'expiryY1', 'expiryY2'];

    // On Backspace: if current input is empty, move focus to previous input
    if (key === 'Backspace') {
      const current = (event.target as HTMLInputElement).value;
      if (!current && index > 0) {
        const inputs = this.expiryInputs.toArray();
        const prev = inputs[index - 1];
        if (prev) {
          try {
            (prev.nativeElement as HTMLInputElement).focus();
            // Also clear previous control if user keeps pressing backspace
            this.paymentForm.get(controlNames[index - 1])?.setValue('');
            (prev.nativeElement as HTMLInputElement).value = '';
          } catch { }
        }
      }
      // allow backspace to proceed
      return;
    }

    // Prevent non-digit keys (allow navigation keys)
    if (!/^\d$/.test(key) && !['ArrowLeft', 'ArrowRight', 'Tab'].includes(key)) {
      event.preventDefault();
    }
  }

  // Helper to know whether expiry is invalid and touched (for displaying errors nicely)
  isExpiryTouchedAndInvalid(): boolean {
    const touched = ['expiryM1', 'expiryM2', 'expiryY1', 'expiryY2'].some(name => this.paymentForm.get(name)?.touched);
    const invalid = !!this.paymentForm.errors;
    return touched && invalid;
  }

  // Get valid years (current year + next 20 years) — kept for backward compatibility if needed elsewhere
  getValidYears(): number[] {
    const currentYear = this.currentDate.getFullYear();
    const years = [];
    for (let i = 0; i <= 20; i++) {
      years.push(currentYear + i);
    }
    return years;
  }

  processPayment(): void {
    if (this.paymentForm.valid && this.appointmentData && this.currentUser) {
      this.isProcessing = true;

      // Build paymentData from form values
      const paymentData: any = {
        patientId: this.currentUser.id,
        amount: this.appointmentData.consultationFee,
        method: this.selectedMethod,
        status: 'PENDING',
        doctorId: this.appointmentData.doctorId,
        appointmentDate: this.appointmentData.appointmentDate,
        appointmentTime: this.parseTimeString(this.appointmentData.appointmentTime),
        endTime: this.parseTimeString(this.appointmentData.endTime),
        appointmentType: 'CONSULTATION',
        symptoms: this.appointmentData.symptoms,
        notes: this.appointmentData.notes || '',
        slotId: this.appointmentData.slotId,
        ...this.paymentForm.value
      };

      // For UPI payments, combine username and bank into upiId
      if (this.selectedMethod === 'UPI') {
        const username = this.paymentForm.get('upiUsername')?.value || '';
        const bank = this.paymentForm.get('upiBank')?.value || '';
        paymentData.upiId = `${username}@${bank}`;
        // Remove the separate fields from payload
        delete paymentData.upiUsername;
        delete paymentData.upiBank;
      }

      // For card payments, combine expiry digits into expiryDate MM/YY
      if (this.selectedMethod === 'CARD') {
        const m1 = this.paymentForm.get('expiryM1')?.value || '';
        const m2 = this.paymentForm.get('expiryM2')?.value || '';
        const y1 = this.paymentForm.get('expiryY1')?.value || '';
        const y2 = this.paymentForm.get('expiryY2')?.value || '';
        const mm = `${m1}${m2}`;
        const yy = `${y1}${y2}`;

        paymentData.expiryDate = `${mm}/${yy}`;
        // Remove the 4 individual fields from payload (optional)
        delete paymentData.expiryM1;
        delete paymentData.expiryM2;
        delete paymentData.expiryY1;
        delete paymentData.expiryY2;
      }

      this.adminService.createPaymentWithAppointment(paymentData).subscribe({
        next: (paymentResponse) => {
          if (paymentResponse.success) {
            // Simulate payment processing
            setTimeout(() => {
              // Confirm payment and create appointment
              this.adminService.confirmPaymentAndCreateAppointment(paymentResponse.data.paymentId).subscribe({
                next: (appointmentResponse) => {
                  if (appointmentResponse.success) {
                    this.isProcessing = false;

                    // Store success data for the success page
                    sessionStorage.setItem('paymentSuccess', JSON.stringify({
                      appointment: appointmentResponse.data,
                      payment: paymentResponse.data,
                      appointmentData: this.appointmentData
                    }));

                    // Clear session data
                    sessionStorage.removeItem('pendingAppointment');
                    sessionStorage.removeItem('selectedPaymentMethod');

                    // Navigate to success page
                    this.router.navigate(['/payments/success']);
                  } else {
                    this.isProcessing = false;
                    this.toastService.showError('Appointment creation failed. Please contact support.');
                    console.error('Appointment creation failed:', appointmentResponse);
                  }
                },
                error: (error) => {
                  this.isProcessing = false;
                  this.toastService.showError('Appointment creation failed. Please contact support.');
                  console.error('Appointment creation error:', error);
                }
              });
            }, 2000);
          } else {
            this.isProcessing = false;
            this.toastService.showError('Payment processing failed. Please try again.');
            console.error('Payment failed:', paymentResponse);
          }
        },
        error: (error) => {
          this.isProcessing = false;
          this.toastService.showError('Payment processing failed. Please try again.');
          console.error('Payment error:', error);
        }
      });
    } else {
      // Touch all controls so validation messages show
      this.markAllControlsTouched();
    }
  }

  private markAllControlsTouched(): void {
    Object.keys(this.paymentForm.controls).forEach(key => {
      this.paymentForm.get(key)?.markAsTouched();
    });
    // Force update to show any form-level errors
    this.paymentForm.updateValueAndValidity();
  }

  parseTimeString(timeStr: string) {
    const [hours, minutes, seconds = 0] = timeStr.split(':').map(Number);
    return {
      hour: hours,
      minute: minutes,
      second: seconds,
      nano: 0
    };
  }

  goBack(): void {
    this.router.navigate(['/payments/select']);
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatTime(time: string): string {
    if (!time) return '';
    return new Date(`2000-01-01T${time}`).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }
}