import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-user-id',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-user-id.component.html',
  styleUrl: './forgot-user-id.component.css'
})
export class ForgotUserIdComponent implements OnInit {
  form!: FormGroup;
  otpSent = signal(false);
  otpSending = signal(false);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  retrievedUserId = signal<string | null>(null);
  devOtp = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group({ accountNumber: ['', [Validators.required]], otp: ['', [Validators.required]] });
  }

  sendOtp(): void {
    const accountNumber = this.form.get('accountNumber')?.value;
    if (!accountNumber) { this.errorMessage.set('Please enter your account number first'); return; }
    this.otpSending.set(true);
    this.errorMessage.set(null);
    this.authService.sendOtp(accountNumber, 'FORGOT_USER_ID').subscribe({
      next: (res) => { this.otpSending.set(false); this.otpSent.set(true); this.devOtp.set(res.data?.devOtp ?? null); },
      error: (err) => { this.otpSending.set(false); this.errorMessage.set(err?.error?.message || 'Failed to send OTP'); }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);
    this.errorMessage.set(null);
    this.authService.forgotUserId(this.form.getRawValue()).subscribe({
      next: (res) => { this.loading.set(false); this.retrievedUserId.set(res.data ?? null); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Could not retrieve User ID'); }
    });
  }
}
