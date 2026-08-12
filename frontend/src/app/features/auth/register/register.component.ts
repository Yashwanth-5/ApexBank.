import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

function passwordsMatchValidator(passwordKey: string, confirmKey: string) {
  return (group: AbstractControl): ValidationErrors | null => {
    const pass = group.get(passwordKey)?.value;
    const confirm = group.get(confirmKey)?.value;
    return pass === confirm ? null : { mismatch: true };
  };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;

  otpSent = signal(false);
  otpSending = signal(false);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  devOtp = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.registerForm = this.fb.group(
      {
        accountNumber: ['', [Validators.required]],
        loginPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmLoginPassword: ['', [Validators.required]],
        transactionPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmTransactionPassword: ['', [Validators.required]],
        otp: ['', [Validators.required]]
      },
      { validators: [passwordsMatchValidator('loginPassword', 'confirmLoginPassword'), passwordsMatchValidator('transactionPassword', 'confirmTransactionPassword')] }
    );
  }

  sendOtp(): void {
    const accountNumber = this.registerForm.get('accountNumber')?.value;
    if (!accountNumber) { this.errorMessage.set('Please enter your account number first'); return; }
    this.otpSending.set(true);
    this.errorMessage.set(null);

    this.authService.sendOtp(accountNumber, 'REGISTER').subscribe({
      next: (res) => { this.otpSending.set(false); this.otpSent.set(true); this.devOtp.set(res.data?.devOtp ?? null); this.successMessage.set('OTP sent to your registered mobile number'); },
      error: (err) => { this.otpSending.set(false); this.errorMessage.set(err?.error?.message || 'Failed to send OTP'); }
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) { this.registerForm.markAllAsTouched(); return; }
    this.loading.set(true);
    this.errorMessage.set(null);

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => { this.loading.set(false); this.successMessage.set('Registration successful! Redirecting to login...'); setTimeout(() => this.router.navigate(['/login']), 1500); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Registration failed. Please try again.'); }
    });
  }
}
