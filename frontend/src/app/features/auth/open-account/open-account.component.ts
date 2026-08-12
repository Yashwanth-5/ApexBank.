import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-open-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './open-account.component.html',
  styleUrl: './open-account.component.css'
})
export class OpenAccountComponent implements OnInit {
  form!: FormGroup;

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  submittedRequestId = signal<number | null>(null);

  occupationTypes = ['Salaried', 'Self Employed', 'Business', 'Student', 'Retired', 'Homemaker'];
  incomeSlabs = ['Below 5 LPA', '5-10 LPA', '10-15 LPA', '15-25 LPA', 'Above 25 LPA'];

  constructor(private fb: FormBuilder, private accountService: AccountService, private router: Router) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group({
      title: ['Mr', [Validators.required]],
      firstName: ['', [Validators.required]],
      middleName: [''],
      lastName: ['', [Validators.required]],
      fatherName: ['', [Validators.required]],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]],
      email: ['', [Validators.email]],
      aadharNumber: ['', [Validators.required, Validators.pattern(/^\d{12}$/)]],
      dateOfBirth: ['', [Validators.required]],
      residentialAddressLine1: ['', [Validators.required]],
      residentialAddressLine2: [''],
      residentialLandmark: [''],
      residentialState: ['', [Validators.required]],
      residentialCity: ['', [Validators.required]],
      residentialPincode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      permanentSameAsResidential: [false],
      permanentAddressLine1: ['', [Validators.required]],
      permanentAddressLine2: [''],
      permanentLandmark: [''],
      permanentState: ['', [Validators.required]],
      permanentCity: ['', [Validators.required]],
      permanentPincode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      occupationType: ['', [Validators.required]],
      sourceOfIncome: ['', [Validators.required]],
      grossAnnualIncome: ['', [Validators.required]],
      wantsDebitCard: [true, [Validators.required]],
      optForNetBanking: [true],
      agreeTerms: [false, [Validators.requiredTrue]]
    });

    this.form.get('permanentSameAsResidential')?.valueChanges.subscribe((checked) => {
      if (checked) this.copyResidentialToPermanent();
    });
  }

  private copyResidentialToPermanent(): void {
    this.form.patchValue({
      permanentAddressLine1: this.form.get('residentialAddressLine1')?.value,
      permanentAddressLine2: this.form.get('residentialAddressLine2')?.value,
      permanentLandmark: this.form.get('residentialLandmark')?.value,
      permanentState: this.form.get('residentialState')?.value,
      permanentCity: this.form.get('residentialCity')?.value,
      permanentPincode: this.form.get('residentialPincode')?.value
    });
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); this.errorMessage.set('Please fill all required fields correctly'); return; }
    if (this.form.get('permanentSameAsResidential')?.value) this.copyResidentialToPermanent();

    this.loading.set(true);
    this.errorMessage.set(null);

    this.accountService.openAccount(this.form.getRawValue() as any).subscribe({
      next: (res) => { this.loading.set(false); this.successMessage.set('Your account opening request has been submitted successfully and is pending admin approval.'); this.submittedRequestId.set(res.data?.id ?? null); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Submission failed. Please try again.'); }
    });
  }
}
