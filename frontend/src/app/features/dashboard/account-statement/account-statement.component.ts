import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DashboardService } from '../../../core/services/dashboard.service';
import { TransactionResponse } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-account-statement',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './account-statement.component.html',
  styleUrl: './account-statement.component.css'
})
export class AccountStatementComponent implements OnInit {
  filterForm!: FormGroup;
  transactions = signal<TransactionResponse[] | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  searched = signal(false);

  constructor(private fb: FormBuilder, private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.filterForm = this.fb.group({
      fromDate: ['', [Validators.required]],
      toDate: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.filterForm.invalid) { this.filterForm.markAllAsTouched(); return; }
    const { fromDate, toDate } = this.filterForm.getRawValue();
    if (fromDate! > toDate!) { this.errorMessage.set('From date cannot be after To date'); return; }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.searched.set(true);

    this.dashboardService.getAccountStatement(fromDate!, toDate!).subscribe({
      next: (res) => { this.transactions.set(res.data ?? []); this.loading.set(false); },
      error: (err) => { this.errorMessage.set(err?.error?.message || 'Failed to load statement'); this.loading.set(false); }
    });
  }

  get totalDebit(): number { return (this.transactions() ?? []).filter(t => t.direction === 'DEBIT').reduce((sum, t) => sum + t.amount, 0); }
  get totalCredit(): number { return (this.transactions() ?? []).filter(t => t.direction === 'CREDIT').reduce((sum, t) => sum + t.amount, 0); }

  printStatement(): void { window.print(); }
}
