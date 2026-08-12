import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DashboardService } from '../../../core/services/dashboard.service';
import { UserProfile } from '../../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  profile = signal<UserProfile | null>(null);
  loading = signal(true);
  editing = signal(false);
  saving = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  form!: FormGroup;

  constructor(private fb: FormBuilder, private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProfile();
  }

  private initForm(): void {
    this.form = this.fb.group({
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]],
      email: ['', [Validators.email]],
      residentialAddressLine1: ['', [Validators.required]],
      residentialAddressLine2: [''],
      residentialLandmark: [''],
      residentialState: ['', [Validators.required]],
      residentialCity: ['', [Validators.required]],
      residentialPincode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      permanentAddressLine1: ['', [Validators.required]],
      permanentAddressLine2: [''],
      permanentLandmark: [''],
      permanentState: ['', [Validators.required]],
      permanentCity: ['', [Validators.required]],
      permanentPincode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      occupationType: ['', [Validators.required]]
    });
  }

  loadProfile(): void {
    this.loading.set(true);
    this.dashboardService.getProfile().subscribe({
      next: (res) => { this.profile.set(res.data ?? null); this.loading.set(false); if (res.data) this.form.patchValue(res.data); },
      error: (err) => { this.errorMessage.set(err?.error?.message || 'Failed to load profile'); this.loading.set(false); }
    });
  }

  enableEdit(): void { this.editing.set(true); this.successMessage.set(null); }
  cancelEdit(): void { this.editing.set(false); if (this.profile()) this.form.patchValue(this.profile()!); }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);
    this.errorMessage.set(null);
    this.dashboardService.updateProfile(this.form.getRawValue()).subscribe({
      next: () => { this.saving.set(false); this.editing.set(false); this.successMessage.set('Profile updated successfully'); this.loadProfile(); },
      error: (err) => { this.saving.set(false); this.errorMessage.set(err?.error?.message || 'Failed to update profile'); }
    });
  }
}
