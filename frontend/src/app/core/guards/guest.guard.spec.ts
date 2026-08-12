import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { AdminAuthService } from '../services/admin-auth.service';
import { guestGuard } from './guest.guard';

describe('guestGuard', () => {
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockAdminAuthService: jasmine.SpyObj<AdminAuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(() => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['getToken']);
    mockAdminAuthService = jasmine.createSpyObj('AdminAuthService', ['getAdminToken']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: AdminAuthService, useValue: mockAdminAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });
  });

  it('should allow access when no tokens exist (guest user)', () => {
    mockAuthService.getToken.and.returnValue(null);
    mockAdminAuthService.getAdminToken.and.returnValue(null);

    const result = TestBed.runInInjectionContext(() => guestGuard());

    expect(result).toBe(true);
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should redirect regular authenticated users to dashboard', () => {
    mockAuthService.getToken.and.returnValue('user-token');
    mockAdminAuthService.getAdminToken.and.returnValue(null);

    const result = TestBed.runInInjectionContext(() => guestGuard());

    expect(result).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should redirect authenticated admins to admin dashboard', () => {
    mockAuthService.getToken.and.returnValue(null);
    mockAdminAuthService.getAdminToken.and.returnValue('admin-token');

    const result = TestBed.runInInjectionContext(() => guestGuard());

    expect(result).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
  });

  it('should prioritize blocking regular users over admin users', () => {
    mockAuthService.getToken.and.returnValue('user-token');
    mockAdminAuthService.getAdminToken.and.returnValue('admin-token');

    const result = TestBed.runInInjectionContext(() => guestGuard());

    expect(result).toBe(false);
    // Regular user token is checked first, so it redirects to dashboard
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']);
  });
});
