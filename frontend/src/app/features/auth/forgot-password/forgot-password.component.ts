import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent implements OnInit {
  form!: FormGroup;
  otpSent = signal(false);
  otpSending = signal(false);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  devOtp = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group({ userId: ['', [Validators.required]], otp: ['', [Validators.required]] });
  }

  sendOtp(): void {
    const userId = this.form.get('userId')?.value;
    if (!userId) { this.errorMessage.set('Please enter your User ID first'); return; }
    this.otpSending.set(true);
    this.errorMessage.set(null);
    this.authService.sendOtp(userId, 'FORGOT_PASSWORD').subscribe({
      next: (res) => { this.otpSending.set(false); this.otpSent.set(true); this.devOtp.set(res.data?.devOtp ?? null); },
      error: (err) => { this.otpSending.set(false); this.errorMessage.set(err?.error?.message || 'Failed to send OTP'); }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);
    this.errorMessage.set(null);
    this.authService.forgotPasswordValidateOtp(this.form.getRawValue()).subscribe({
      next: () => { this.loading.set(false); this.router.navigate(['/set-new-password'], { queryParams: { userId: this.form.get('userId')?.value } }); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'OTP verification failed'); }
    });
  }
}
