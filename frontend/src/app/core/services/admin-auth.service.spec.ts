import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AdminAuthService } from './admin-auth.service';
import { ApiResponse, LoginResponse } from '../models/user.model';

describe('AdminAuthService', () => {
  let service: AdminAuthService;
  let httpMock: HttpTestingController;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(() => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AdminAuthService,
        { provide: Router, useValue: mockRouter }
      ]
    });

    service = TestBed.inject(AdminAuthService);
    httpMock = TestBed.inject(HttpTestingController);

    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  describe('login', () => {
    it('should store admin token and user data on successful admin login', (done) => {
      const loginResponse: ApiResponse<LoginResponse> = {
        success: true,
        data: {
          userId: 'admin1',
          token: 'admin-token-123',
          role: 'ADMIN'
        }
      };

      service.login({ userId: 'admin1', password: 'password' }).subscribe(() => {
        expect(localStorage.getItem('apexbank_admin_token')).toBe('admin-token-123');
        expect(service.getAdminToken()).toBe('admin-token-123');
        expect(service.currentAdmin()?.role).toBe('ADMIN');
        expect(service.isAdminAuthed()).toBe(true);
        done();
      });

      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      req.flush(loginResponse);
    });

    it('should throw error when non-admin user tries to login', (done) => {
      const loginResponse: ApiResponse<LoginResponse> = {
        success: true,
        data: {
          userId: 'user1',
          token: 'user-token-123',
          role: 'USER'
        }
      };

      service.login({ userId: 'user1', password: 'password' }).subscribe({
        next: () => {
          fail('Should have thrown an error for non-admin user');
        },
        error: (err) => {
          expect(err.error.message).toContain('administrators only');
          done();
        }
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush(loginResponse);
    });
  });

  describe('logout', () => {
    it('should clear admin token and user data on logout', () => {
      localStorage.setItem('apexbank_admin_token', 'admin-token-123');
      localStorage.setItem('apexbank_admin_user', JSON.stringify({ userId: 'admin1', role: 'ADMIN' }));
      service.currentAdmin.set({ userId: 'admin1', role: 'ADMIN', token: 'admin-token-123' });
      service.isAdminAuthed.set(true);

      service.logout();

      expect(localStorage.getItem('apexbank_admin_token')).toBeNull();
      expect(localStorage.getItem('apexbank_admin_user')).toBeNull();
      expect(service.currentAdmin()).toBeNull();
      expect(service.isAdminAuthed()).toBe(false);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/login']);
    });
  });

  describe('getAdminToken', () => {
    it('should return token from localStorage if it exists', () => {
      localStorage.setItem('apexbank_admin_token', 'test-token');
      expect(service.getAdminToken()).toBe('test-token');
    });

    it('should return null if no token in localStorage', () => {
      expect(service.getAdminToken()).toBeNull();
    });
  });

  describe('Token Persistence', () => {
    it('should restore token and user on service initialization', () => {
      const adminData = { userId: 'admin1', role: 'ADMIN', token: 'admin-token-123' };
      localStorage.setItem('apexbank_admin_token', 'admin-token-123');
      localStorage.setItem('apexbank_admin_user', JSON.stringify(adminData));

      // Create a new service instance to trigger initialization
      const newService = new AdminAuthService(TestBed.inject(HttpClientTestingModule as any), mockRouter);

      expect(newService.getAdminToken()).toBe('admin-token-123');
      expect(newService.currentAdmin()?.userId).toBe('admin1');
      expect(newService.isAdminAuthed()).toBe(true);
    });

    it('should sync signals with localStorage on initialization', () => {
      const adminData = { userId: 'admin1', role: 'ADMIN', token: 'admin-token-123' };
      localStorage.setItem('apexbank_admin_token', 'admin-token-123');
      localStorage.setItem('apexbank_admin_user', JSON.stringify(adminData));

      const newService = new AdminAuthService(TestBed.inject(HttpClientTestingModule as any), mockRouter);

      expect(newService.currentAdmin()).toEqual(adminData);
      expect(newService.isAdminAuthed()).toBe(true);
    });
  });
});
