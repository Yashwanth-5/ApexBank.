import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

function passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
  const pass = group.get('newLoginPassword')?.value;
  const confirm = group.get('confirmLoginPassword')?.value;
  return pass === confirm ? null : { mismatch: true };
}

@Component({
  selector: 'app-set-new-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './set-new-password.component.html',
  styleUrl: './set-new-password.component.css'
})
export class SetNewPasswordComponent implements OnInit {
  userId = '';
  form!: FormGroup;

  loading = signal(false);
  showPassword = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.userId = this.route.snapshot.queryParamMap.get('userId') ?? '';
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group(
      { newLoginPassword: ['', [Validators.required, Validators.minLength(8)]], confirmLoginPassword: ['', [Validators.required]] },
      { validators: [passwordsMatchValidator] }
    );
  }

  onSubmit(): void {
    if (this.form.invalid || !this.userId) {
      this.form.markAllAsTouched();
      if (!this.userId) this.errorMessage.set('Session expired. Please restart the forgot password process.');
      return;
    }
    this.loading.set(true);
    this.errorMessage.set(null);
    const payload = { userId: this.userId, newLoginPassword: this.form.get('newLoginPassword')?.value, confirmLoginPassword: this.form.get('confirmLoginPassword')?.value };
    this.authService.setNewPassword(payload).subscribe({
      next: () => { this.loading.set(false); this.successMessage.set('Password reset successful! Redirecting to login...'); setTimeout(() => this.router.navigate(['/login']), 1500); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Failed to reset password'); }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.set(!this.showPassword());
  }
}
