import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { AdminAuthService } from '../services/admin-auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const adminAuthService = inject(AdminAuthService);
  const router = inject(Router);

  // Block regular authenticated users
  if (authService.getToken()) {
    router.navigate(['/dashboard']);
    return false;
  }

  // Block authenticated admins from accessing regular login routes
  if (adminAuthService.getAdminToken()) {
    router.navigate(['/admin/dashboard']);
    return false;
  }

  return true;
};
