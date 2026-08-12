import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService, AdminAccountResponse, AdminSystemStats, AdminTransactionResponse } from '../../../core/services/admin.service';
import { AccountOpeningRequestResponse } from '../../../core/models/account-request.model';

type FilterType = 'PENDING' | 'ALL';
type ViewType = 'dashboard' | 'requests' | 'users' | 'transactions' | 'credit';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  // ── View Management ────────────────────────────────────────────────────────
  currentView = signal<ViewType>('dashboard');

  // ── Common State ───────────────────────────────────────────────────────────
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  actionLoading = signal(false);

  // ── Account Requests ───────────────────────────────────────────────────────
  requests = signal<AccountOpeningRequestResponse[]>([]);
  filter = signal<FilterType>('PENDING');
  selectedRequest = signal<AccountOpeningRequestResponse | null>(null);
  showRejectModal = signal(false);
  rejectReason = '';

  // ── Users ──────────────────────────────────────────────────────────────────
  accounts = signal<AdminAccountResponse[]>([]);
  selectedAccount = signal<AdminAccountResponse | null>(null);

  // ── Transactions ───────────────────────────────────────────────────────────
  transactions = signal<AdminTransactionResponse[]>([]);

  // ── Stats ──────────────────────────────────────────────────────────────────
  stats = signal<AdminSystemStats | null>(null);

  // ── Credit Form ────────────────────────────────────────────────────────────
  creditForm!: FormGroup;

  constructor(
    private adminService: AdminService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.determineView();
    this.initCreditForm();
    this.loadViewData();
  }

  private determineView(): void {
    const url = this.router.url;
    if (url.includes('/admin/dashboard')) this.currentView.set('dashboard');
    else if (url.includes('/admin/requests')) this.currentView.set('requests');
    else if (url.includes('/admin/users')) this.currentView.set('users');
    else if (url.includes('/admin/transactions')) this.currentView.set('transactions');
    else if (url.includes('/admin/credit')) this.currentView.set('credit');
    else this.currentView.set('dashboard');
  }

  private initCreditForm(): void {
    this.creditForm = this.fb.group({
      accountNumber: ['', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      remarks: ['']
    });
  }

  private loadViewData(): void {
    this.clearMessages();
    const view = this.currentView();
    
    if (view === 'dashboard') this.loadStats();
    else if (view === 'requests') this.loadRequests();
    else if (view === 'users') this.loadAccounts();
    else if (view === 'transactions') this.loadTransactions();
  }

  // ── Data Loading ───────────────────────────────────────────────────────────

  private loadStats(): void {
    this.loading.set(true);
    this.adminService.getSystemStats().subscribe({
      next: (res) => { this.stats.set(res.data ?? null); this.loading.set(false); },
      error: (err) => { this.setError(err?.error?.message || 'Failed to load stats'); this.loading.set(false); }
    });
  }

  private loadRequests(): void {
    this.loading.set(true);
    const call = this.filter() === 'PENDING' ? 
      this.adminService.getPendingRequests() : this.adminService.getAllRequests();
    
    call.subscribe({
      next: (res) => { this.requests.set(res.data ?? []); this.loading.set(false); },
      error: (err) => { this.setError(err?.error?.message || 'Failed to load requests'); this.loading.set(false); }
    });
  }

  private loadAccounts(): void {
    this.loading.set(true);
    this.adminService.getAllAccounts().subscribe({
      next: (res) => { this.accounts.set(res.data ?? []); this.loading.set(false); },
      error: (err) => { this.setError(err?.error?.message || 'Failed to load accounts'); this.loading.set(false); }
    });
  }

  private loadTransactions(): void {
    this.loading.set(true);
    this.adminService.getAllTransactions().subscribe({
      next: (res) => { this.transactions.set(res.data ?? []); this.loading.set(false); },
      error: (err) => { this.setError(err?.error?.message || 'Failed to load transactions'); this.loading.set(false); }
    });
  }

  // ── Request Actions ────────────────────────────────────────────────────────

  setFilter(f: FilterType): void { 
    this.filter.set(f); 
    this.loadRequests(); 
  }

  viewRequestDetails(req: AccountOpeningRequestResponse): void { 
    this.selectedRequest.set(req); 
  }

  closeDetails(): void { 
    this.selectedRequest.set(null); 
  }

  approve(id: number): void {
    this.actionLoading.set(true);
    this.adminService.approveRequest(id).subscribe({
      next: (res) => {
        this.actionLoading.set(false);
        this.setSuccess(res.message);
        this.selectedRequest.set(null);
        this.loadRequests();
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.setError(err?.error?.message || 'Failed to approve request');
      }
    });
  }

  openRejectModal(): void { 
    this.rejectReason = ''; 
    this.showRejectModal.set(true); 
  }

  confirmReject(): void {
    const req = this.selectedRequest();
    if (!req || !this.rejectReason.trim()) return;
    
    this.actionLoading.set(true);
    this.adminService.rejectRequest(req.id, this.rejectReason).subscribe({
      next: (res) => {
        this.actionLoading.set(false);
        this.setSuccess(res.message);
        this.showRejectModal.set(false);
        this.selectedRequest.set(null);
        this.loadRequests();
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.setError(err?.error?.message || 'Failed to reject request');
      }
    });
  }

  // ── User Actions ───────────────────────────────────────────────────────────

  viewUserDetails(acc: AdminAccountResponse): void { 
    this.selectedAccount.set(acc); 
  }

  closeUserDetails(): void { 
    this.selectedAccount.set(null); 
  }

  // ── Credit Actions ─────────────────────────────────────────────────────────

  onCreditSubmit(): void {
    if (this.creditForm.invalid) { 
      this.creditForm.markAllAsTouched(); 
      return; 
    }

    this.loading.set(true);
    const formValue = this.creditForm.getRawValue();
    
    this.adminService.creditAccount({
      accountNumber: formValue.accountNumber,
      amount: parseFloat(formValue.amount),
      remarks: formValue.remarks || undefined
    }).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.setSuccess(`Successfully credited ₹${this.formatAmount(formValue.amount)} to account ${formValue.accountNumber}`);
        this.creditForm.reset();
      },
      error: (err) => {
        this.loading.set(false);
        this.setError(err?.error?.message || 'Failed to credit account');
      }
    });
  }

  // ── Utilities ──────────────────────────────────────────────────────────────

  formatAmount(amount: number): string {
    return amount.toLocaleString('en-IN', { 
      minimumFractionDigits: 2, 
      maximumFractionDigits: 2 
    });
  }

  private setError(message: string): void {
    this.errorMessage.set(message);
    this.successMessage.set(null);
    setTimeout(() => this.errorMessage.set(null), 5000);
  }

  private setSuccess(message: string): void {
    this.successMessage.set(message);
    this.errorMessage.set(null);
    setTimeout(() => this.successMessage.set(null), 5000);
  }

  private clearMessages(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }
}