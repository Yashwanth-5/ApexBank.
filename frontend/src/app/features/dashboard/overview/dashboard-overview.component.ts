import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { DashboardResponse, TransactionResponse } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-dashboard-overview',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-overview.component.html',
  styleUrl: './dashboard-overview.component.css'
})
export class DashboardOverviewComponent implements OnInit {
  dashboard = signal<DashboardResponse | null>(null);
  recentTransactions = signal<TransactionResponse[]>([]);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  balanceVisible = signal(true);

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getDashboard().subscribe({
      next: (res) => { this.dashboard.set(res.data ?? null); this.loading.set(false); },
      error: (err) => { this.errorMessage.set(err?.error?.message || 'Failed to load dashboard'); this.loading.set(false); }
    });

    this.dashboardService.getRecentTransactions().subscribe({
      next: (res) => this.recentTransactions.set(res.data ?? []),
      error: () => {}
    });
  }

  toggleBalance(): void { this.balanceVisible.update(v => !v); }
}
