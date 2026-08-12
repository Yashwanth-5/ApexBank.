import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, LoginRequest, LoginResponse } from '../models/user.model';

const ADMIN_TOKEN_KEY = 'apexbank_admin_token';
const ADMIN_USER_KEY  = 'apexbank_admin_user';

@Injectable({ providedIn: 'root' })
export class AdminAuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  /** Mirrors AuthService shape so guards / interceptors are consistent. */
  currentAdmin  = signal<LoginResponse | null>(this.loadStoredAdmin());
  isAdminAuthed = signal<boolean>(!!this.getAdminToken());

  constructor(private http: HttpClient, private router: Router) {
    // Ensure signals are in sync with storage
    this.syncSignalsWithStorage();
  }

  /** Reuses the same /auth/login endpoint — the JWT will carry role=ADMIN. */
  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, request).pipe(
      tap(res => {
        if (res.success && res.data) {
          if (res.data.role !== 'ADMIN') {
            throw { error: { message: 'Access denied. This portal is for administrators only.' } };
          }
          this.storeSession(res.data);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(ADMIN_TOKEN_KEY);
    localStorage.removeItem(ADMIN_USER_KEY);
    this.currentAdmin.set(null);
    this.isAdminAuthed.set(false);
    this.router.navigate(['/admin/login']);
  }

  getAdminToken(): string | null {
    return localStorage.getItem(ADMIN_TOKEN_KEY);
  }

  private storeSession(data: LoginResponse): void {
    localStorage.setItem(ADMIN_TOKEN_KEY, data.token);
    localStorage.setItem(ADMIN_USER_KEY, JSON.stringify(data));
    this.currentAdmin.set(data);
    this.isAdminAuthed.set(true);
  }

  private loadStoredAdmin(): LoginResponse | null {
    const raw = localStorage.getItem(ADMIN_USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  /** Sync signals with localStorage in case they get out of sync */
  private syncSignalsWithStorage(): void {
    const token = this.getAdminToken();
    const admin = this.loadStoredAdmin();
    this.currentAdmin.set(admin);
    this.isAdminAuthed.set(!!token);
  }
}
