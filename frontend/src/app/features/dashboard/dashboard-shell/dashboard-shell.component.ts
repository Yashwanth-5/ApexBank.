import { Component, HostListener, OnInit, Signal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';
import { LoginResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-dashboard-shell',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './dashboard-shell.component.html',
  styleUrl: './dashboard-shell.component.css'
})
export class DashboardShellComponent implements OnInit {
  sidebarOpen = signal(false);
  user!: Signal<LoginResponse | null>;

  navItems = [
    { path: '/dashboard', label: 'Overview', icon: 'dashboard' },
    { path: '/dashboard/account-summary', label: 'Accounts', icon: 'summary' },
    { path: '/dashboard/account-statement', label: 'Statements', icon: 'statement' },
    { path: '/fund-transfer', label: 'Transfer Money', icon: 'transfer' },
    { path: '/payees', label: 'Beneficiaries', icon: 'payees' },
    { path: '/dashboard/profile', label: 'User Profile', icon: 'profile' },
    { path: '/dashboard/change-password', label: 'Security', icon: 'password' }
  ];

  constructor(private authService: AuthService,
              private router: Router,
              public themeService: ThemeService) {}

  ngOnInit(): void {
    this.user = this.authService.currentUser;
    history.pushState(null, '', location.href);
  }

  @HostListener('window:popstate')
  onPopState(): void {
    if (!this.authService.getToken()) this.router.navigate(['/session-expired']);
    else history.pushState(null, '', location.href);
  }

  toggleSidebar(): void { this.sidebarOpen.update(v => !v); }

  logout(): void {
    localStorage.setItem('apexbank_last_login', new Date().toLocaleString());
    this.authService.logout();
  }
}
