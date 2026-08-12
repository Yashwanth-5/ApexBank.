import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FundTransferService } from '../../../core/services/fund-transfer.service';
import { PayeeResponse } from '../../../core/models/transaction.model';

type TransferModeType = 'NEFT' | 'UPI';
type UpiTargetType = 'UPI_ID' | 'ACCOUNT';

@Component({
  selector: 'app-fund-transfer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './fund-transfer.component.html',
  styleUrl: './fund-transfer.component.css'
})
export class FundTransferComponent implements OnInit {
  mode = signal<TransferModeType>('NEFT');
  upiTarget = signal<UpiTargetType>('UPI_ID');
  payees = signal<PayeeResponse[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  neftForm!: FormGroup;
  upiForm!: FormGroup;

  constructor(private fb: FormBuilder, private fundTransferService: FundTransferService, private router: Router) {}

  ngOnInit(): void {
    this.initForms();
    this.fundTransferService.getPayees().subscribe({ next: (res) => this.payees.set(res.data ?? []), error: () => {} });
  }

  private initForms(): void {
    this.neftForm = this.fb.group({
      toAccountNumber: ['', [Validators.required]],
      amount: [null as number | null, [Validators.required, Validators.min(1)]],
      remarks: [''],
      transactionPassword: ['', [Validators.required]]
    });

    this.upiForm = this.fb.group({
      toUpiId: [''],
      toAccountNumber: [''],
      amount: [null as number | null, [Validators.required, Validators.min(1)]],
      remarks: [''],
      transactionPassword: ['', [Validators.required]]
    });
  }

  setMode(m: TransferModeType): void { this.mode.set(m); this.errorMessage.set(null); }
  setUpiTarget(t: UpiTargetType): void { this.upiTarget.set(t); this.upiForm.patchValue({ toUpiId: '', toAccountNumber: '' }); }
  selectPayeeForNeft(accountNumber: string): void { this.neftForm.patchValue({ toAccountNumber: accountNumber }); }
  selectPayeeForUpi(accountNumber: string): void { this.upiTarget.set('ACCOUNT'); this.upiForm.patchValue({ toAccountNumber: accountNumber, toUpiId: '' }); }

  submitNeft(): void {
    if (this.neftForm.invalid) { this.neftForm.markAllAsTouched(); return; }
    this.loading.set(true);
    this.errorMessage.set(null);

    this.fundTransferService.transferNeft(this.neftForm.getRawValue()).subscribe({
      next: (res) => { this.loading.set(false); this.router.navigate(['/transfer-success'], { state: { result: res.data } }); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Transfer failed'); }
    });
  }

  submitUpi(): void {
    const target = this.upiTarget();
    const value = target === 'UPI_ID' ? this.upiForm.get('toUpiId')?.value : this.upiForm.get('toAccountNumber')?.value;

    if (!value) { this.errorMessage.set(target === 'UPI_ID' ? 'Please enter a UPI ID' : 'Please enter an account number'); return; }
    if (this.upiForm.get('amount')?.invalid || this.upiForm.get('transactionPassword')?.invalid) { this.upiForm.markAllAsTouched(); return; }

    this.loading.set(true);
    this.errorMessage.set(null);

    const payload = {
      toUpiId: target === 'UPI_ID' ? value : null,
      toAccountNumber: target === 'ACCOUNT' ? value : null,
      amount: this.upiForm.get('amount')?.value,
      remarks: this.upiForm.get('remarks')?.value,
      transactionPassword: this.upiForm.get('transactionPassword')?.value
    };

    this.fundTransferService.transferUpi(payload).subscribe({
      next: (res) => { this.loading.set(false); this.router.navigate(['/transfer-success'], { state: { result: res.data } }); },
      error: (err) => { this.loading.set(false); this.errorMessage.set(err?.error?.message || 'Transfer failed'); }
    });
  }
}
