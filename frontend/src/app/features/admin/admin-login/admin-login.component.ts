import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AdminAuthService } from '../../../core/services/admin-auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.css'
})
export class AdminLoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage = signal<string | null>(null);
  loading       = signal(false);
  showPassword  = signal(false);

  constructor(
    private fb: FormBuilder,
    private adminAuthService: AdminAuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // If already authenticated as admin, go straight to dashboard
    if (this.adminAuthService.getAdminToken() && this.adminAuthService.currentAdmin()?.role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
      return;
    }
    this.loginForm = this.fb.group({
      adminId:  ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) { this.loginForm.markAllAsTouched(); return; }
    this.errorMessage.set(null);
    this.loading.set(true);

    const { adminId, password } = this.loginForm.getRawValue();
    this.adminAuthService.login({ userId: adminId, password }).subscribe({
      next: () => {
        this.loading.set(false);
        // Force navigation to admin dashboard after successful login
        this.router.navigate(['/admin/dashboard']).catch(err => {
          console.error('Navigation error:', err);
        });
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err?.error?.message || 'Login failed. Invalid credentials or insufficient privileges.'
        );
      }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.set(!this.showPassword());
  }
}
