import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminLoginComponent } from './admin-login.component';
import { AdminAuthService } from '../../../core/services/admin-auth.service';

describe('AdminLoginComponent', () => {
  let component: AdminLoginComponent;
  let fixture: ComponentFixture<AdminLoginComponent>;
  let mockAdminAuthService: jasmine.SpyObj<AdminAuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockAdminAuthService = jasmine.createSpyObj('AdminAuthService', ['login', 'getAdminToken'], {
      currentAdmin: jasmine.createSpy().and.returnValue(null)
    });
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [AdminLoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AdminAuthService, useValue: mockAdminAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AdminLoginComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should navigate to admin dashboard if already authenticated', () => {
      mockAdminAuthService.getAdminToken.and.returnValue('admin-token');
      mockAdminAuthService.currentAdmin.and.returnValue({ role: 'ADMIN' } as any);

      component.ngOnInit();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
    });

    it('should create login form if not authenticated', () => {
      mockAdminAuthService.getAdminToken.and.returnValue(null);
      mockAdminAuthService.currentAdmin.and.returnValue(null);

      component.ngOnInit();

      expect(component.loginForm).toBeDefined();
      expect(component.loginForm.get('adminId')).toBeDefined();
      expect(component.loginForm.get('password')).toBeDefined();
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      mockAdminAuthService.getAdminToken.and.returnValue(null);
      fixture.detectChanges(); // This calls ngOnInit
    });

    it('should mark form as touched if invalid', () => {
      component.loginForm.patchValue({ adminId: '', password: '' });

      component.onSubmit();

      expect(component.loginForm.touched).toBe(true);
      expect(mockAdminAuthService.login).not.toHaveBeenCalled();
    });

    it('should call login service with credentials', () => {
      mockAdminAuthService.login.and.returnValue(of({} as any));
      component.loginForm.patchValue({ adminId: 'admin1', password: 'password123' });

      component.onSubmit();

      expect(mockAdminAuthService.login).toHaveBeenCalledWith({
        userId: 'admin1',
        password: 'password123'
      });
    });

    it('should navigate to admin dashboard on successful login', (done) => {
      mockAdminAuthService.login.and.returnValue(of({} as any));
      component.loginForm.patchValue({ adminId: 'admin1', password: 'password123' });
      mockRouter.navigate.and.returnValue(Promise.resolve(true));

      component.onSubmit();

      setTimeout(() => {
        expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
        done();
      }, 50);
    });

    it('should display error message on login failure', () => {
      const errorResponse = { error: { message: 'Invalid credentials' } };
      mockAdminAuthService.login.and.returnValue(throwError(() => errorResponse));
      component.loginForm.patchValue({ adminId: 'admin1', password: 'wrong' });

      component.onSubmit();

      setTimeout(() => {
        expect(component.errorMessage()).toBe('Invalid credentials');
      }, 50);
    });

    it('should display default error message when no error details provided', () => {
      mockAdminAuthService.login.and.returnValue(throwError(() => ({})));
      component.loginForm.patchValue({ adminId: 'admin1', password: 'wrong' });

      component.onSubmit();

      setTimeout(() => {
        expect(component.errorMessage()).toContain('Login failed');
      }, 50);
    });

    it('should set loading state during login', () => {
      mockAdminAuthService.login.and.returnValue(of({} as any));
      component.loginForm.patchValue({ adminId: 'admin1', password: 'password123' });

      expect(component.loading()).toBe(false);
      component.onSubmit();
      expect(component.loading()).toBe(true);
      // After successful login, loading is reset to false
    });
  });

  describe('togglePasswordVisibility', () => {
    it('should toggle password visibility', () => {
      expect(component.showPassword()).toBe(false);
      component.togglePasswordVisibility();
      expect(component.showPassword()).toBe(true);
      component.togglePasswordVisibility();
      expect(component.showPassword()).toBe(false);
    });
  });
});
