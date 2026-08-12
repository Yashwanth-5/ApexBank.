import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AdminAuthService } from '../services/admin-auth.service';
import { adminAuthGuard } from './admin-auth.guard';

describe('adminAuthGuard', () => {
  let mockAdminAuthService: jasmine.SpyObj<AdminAuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(() => {
    mockAdminAuthService = jasmine.createSpyObj('AdminAuthService', ['getAdminToken', 'logout'], {
      currentAdmin: jasmine.createSpy().and.returnValue({ role: 'ADMIN' })
    });
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AdminAuthService, useValue: mockAdminAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });
  });

  it('should allow access when admin token exists and role is ADMIN', () => {
    mockAdminAuthService.getAdminToken.and.returnValue('valid-token');
    mockAdminAuthService.currentAdmin.and.returnValue({ role: 'ADMIN' } as any);

    const result = TestBed.runInInjectionContext(() => adminAuthGuard());

    expect(result).toBe(true);
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should deny access and redirect to login when no token exists', () => {
    mockAdminAuthService.getAdminToken.and.returnValue(null);
    mockAdminAuthService.currentAdmin.and.returnValue(null);

    const result = TestBed.runInInjectionContext(() => adminAuthGuard());

    expect(result).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/login']);
  });

  it('should deny access and redirect to login when token exists but role is not ADMIN', () => {
    mockAdminAuthService.getAdminToken.and.returnValue('valid-token');
    mockAdminAuthService.currentAdmin.and.returnValue({ role: 'USER' } as any);

    const result = TestBed.runInInjectionContext(() => adminAuthGuard());

    expect(result).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/login']);
  });

  it('should logout and redirect when token exists but currentAdmin is null', () => {
    mockAdminAuthService.getAdminToken.and.returnValue('valid-token');
    mockAdminAuthService.currentAdmin.and.returnValue(null);

    const result = TestBed.runInInjectionContext(() => adminAuthGuard());

    expect(result).toBe(false);
    expect(mockAdminAuthService.logout).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/login']);
  });

  it('should logout and redirect when currentAdmin has invalid data', () => {
    mockAdminAuthService.getAdminToken.and.returnValue('valid-token');
    mockAdminAuthService.currentAdmin.and.returnValue({ role: 'INVALID' } as any);

    const result = TestBed.runInInjectionContext(() => adminAuthGuard());

    expect(result).toBe(false);
    expect(mockAdminAuthService.logout).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/login']);
  });
});
