import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { AdminAuthService } from '../services/admin-auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService      = inject(AuthService);
  const adminAuthService = inject(AdminAuthService);

  const isPublicEndpoint =
    req.url.includes('/auth/') ||
    (req.url.includes('/accounts/open') && req.method === 'POST');

  if (!isPublicEndpoint) {
    // Admin routes: prefer the admin token so the gateway sees ADMIN role
    if (req.url.includes('/admin/')) {
      const adminToken = adminAuthService.getAdminToken();
      if (adminToken) {
        req = req.clone({ setHeaders: { Authorization: `Bearer ${adminToken}` } });
        return next(req);
      }
    }

    // All other protected routes: use the regular user token
    const token = authService.getToken();
    if (token) {
      req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    }
  }

  return next(req);
};
