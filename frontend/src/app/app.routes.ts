import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { adminAuthGuard } from './core/guards/admin-auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  { path: 'home', loadComponent: () => import('./features/auth/home/home.component').then(m => m.HomeComponent) },
  { path: 'login', canActivate: [guestGuard], loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', canActivate: [guestGuard], loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
  { path: 'open-account', loadComponent: () => import('./features/auth/open-account/open-account.component').then(m => m.OpenAccountComponent) },
  { path: 'forgot-user-id', loadComponent: () => import('./features/auth/forgot-user-id/forgot-user-id.component').then(m => m.ForgotUserIdComponent) },
  { path: 'forgot-password', loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) },
  { path: 'set-new-password', loadComponent: () => import('./features/auth/set-new-password/set-new-password.component').then(m => m.SetNewPasswordComponent) },
  { path: 'account-locked', loadComponent: () => import('./features/auth/account-locked/account-locked.component').then(m => m.AccountLockedComponent) },
  { path: 'session-expired', loadComponent: () => import('./features/auth/session-expired/session-expired.component').then(m => m.SessionExpiredComponent) },

  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard-shell/dashboard-shell.component').then(m => m.DashboardShellComponent),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/overview/dashboard-overview.component').then(m => m.DashboardOverviewComponent) },
      { path: 'dashboard/account-summary', loadComponent: () => import('./features/dashboard/account-summary/account-summary.component').then(m => m.AccountSummaryComponent) },
      { path: 'dashboard/account-statement', loadComponent: () => import('./features/dashboard/account-statement/account-statement.component').then(m => m.AccountStatementComponent) },
      { path: 'dashboard/profile', loadComponent: () => import('./features/dashboard/profile/profile.component').then(m => m.ProfileComponent) },
      { path: 'dashboard/change-password', loadComponent: () => import('./features/dashboard/change-password/change-password.component').then(m => m.ChangePasswordComponent) },
      { path: 'payees', loadComponent: () => import('./features/fund-transfer/add-payee/add-payee.component').then(m => m.AddPayeeComponent) },
      { path: 'fund-transfer', loadComponent: () => import('./features/fund-transfer/fund-transfer/fund-transfer.component').then(m => m.FundTransferComponent) },
      { path: 'transfer-success', loadComponent: () => import('./features/fund-transfer/transfer-success/transfer-success.component').then(m => m.TransferSuccessComponent) }
    ]
  },

  // ── Admin Routes ──────────────────────────────────────────────────────────

  { path: 'admin/login', canActivate: [guestGuard], loadComponent: () => import('./features/admin/admin-login/admin-login.component').then(m => m.AdminLoginComponent) },

  {
    path: 'admin',
    canActivate: [adminAuthGuard],
    loadComponent: () => import('./features/admin/admin-shell/admin-shell.component').then(m => m.AdminShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'requests', loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'users', loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'transactions', loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'credit', loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) }
    ]
  },

  { path: '**', redirectTo: 'home' }
];
