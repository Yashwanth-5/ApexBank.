import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { AccountSummaryResponse, TransactionResponse } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-account-summary',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './account-summary.component.html',
  styleUrl: './account-summary.component.css'
})
export class AccountSummaryComponent implements OnInit {
  summary = signal<AccountSummaryResponse | null>(null);
  recentTransactions = signal<TransactionResponse[]>([]);
  loading = signal(true);
  errorMessage = signal<string | null>(null);

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getAccountSummary().subscribe({
      next: (res) => { this.summary.set(res.data ?? null); this.loading.set(false); },
      error: (err) => { this.errorMessage.set(err?.error?.message || 'Failed to load account summary'); this.loading.set(false); }
    });
    this.dashboardService.getRecentTransactions().subscribe({
      next: (res) => this.recentTransactions.set(res.data ?? []),
      error: () => {}
    });
  }
}
