import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FundTransferService } from '../../../core/services/fund-transfer.service';
import { PayeeResponse } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-add-payee',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-payee.component.html',
  styleUrl: './add-payee.component.css'
})
export class AddPayeeComponent implements OnInit {
  form!: FormGroup;

  payees = signal<PayeeResponse[]>([]);
  loading = signal(false);
  loadingList = signal(true);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  constructor(private fb: FormBuilder, private fundTransferService: FundTransferService, private router: Router) {}

  ngOnInit(): void {
    this.initForm();
    this.loadPayees();
  }

  private initForm(): void {
    this.form = this.fb.group({
      payeeName: ['', [Validators.required]],
      payeeAccountNumber: ['', [Validators.required]],
      confirmPayeeAccountNumber: ['', [Validators.required]],
      nickname: ['']
    });
  }

  loadPayees(): void {
    this.loadingList.set(true);
    this.fundTransferService.getPayees().subscribe({
      next: (res) => { this.payees.set(res.data ?? []); this.loadingList.set(false); },
      error: () => this.loadingList.set(false)
    });
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    if (this.form.get('payeeAccountNumber')?.value !== this.form.get('confirmPayeeAccountNumber')?.value) {
      this.errorMessage.set('Account number and confirm account number do not match');
      return;
    }
    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.fundTransferService.addPayee(this.form.getRawValue()).subscribe({
      next: () => { this.loading.set(false); this.successMessage.set('Beneficiary saved successfully'); this.form.reset(); this.loadPayees(); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Failed to save beneficiary'); }
    });
  }

  deletePayee(id: number): void {
    this.fundTransferService.deletePayee(id).subscribe({
      next: () => this.loadPayees(),
      error: (err) => this.errorMessage.set(err?.error?.message || 'Failed to remove beneficiary')
    });
  }

  goToTransfer(): void { this.router.navigate(['/fund-transfer']); }
}
