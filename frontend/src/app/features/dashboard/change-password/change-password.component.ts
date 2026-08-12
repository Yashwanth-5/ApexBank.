import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

function matchValidator(a: string, b: string) {
  return (group: AbstractControl): ValidationErrors | null => group.get(a)?.value === group.get(b)?.value ? null : { mismatch: true };
}

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent implements OnInit {
  form!: FormGroup;

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group(
      {
        currentLoginPassword: ['', [Validators.required]],
        newLoginPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmNewLoginPassword: ['', [Validators.required]],
        currentTransactionPassword: ['', [Validators.required]],
        newTransactionPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmNewTransactionPassword: ['', [Validators.required]]
      },
      { validators: [matchValidator('newLoginPassword', 'confirmNewLoginPassword'), matchValidator('newTransactionPassword', 'confirmNewTransactionPassword')] }
    );
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.authService.changePassword(this.form.getRawValue()).subscribe({
      next: () => { this.loading.set(false); this.successMessage.set('Password changed successfully'); this.form.reset(); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Failed to change password'); }
    });
  }
}
