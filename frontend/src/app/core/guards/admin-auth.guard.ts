import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AdminAuthService } from '../services/admin-auth.service';

export const adminAuthGuard: CanActivateFn = () => {
  const adminAuthService = inject(AdminAuthService);
  const router = inject(Router);

  const hasAdminToken = !!adminAuthService.getAdminToken();
  const isAdminRole = adminAuthService.currentAdmin()?.role === 'ADMIN';

  if (hasAdminToken && isAdminRole) {
    return true;
  }

  // Redirect to admin login if not authenticated
  router.navigate(['/admin/login']);
  return false;
};