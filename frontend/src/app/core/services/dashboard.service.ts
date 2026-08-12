import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { AccountSummaryResponse, DashboardResponse, TransactionResponse } from '../models/transaction.model';
import { UserProfile } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly apiUrl = `${environment.apiUrl}/dashboard`;
  private readonly txnUrl = `${environment.apiUrl}/fund-transfer`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<ApiResponse<DashboardResponse>> {
    return this.http.get<ApiResponse<DashboardResponse>>(this.apiUrl);
  }

  getAccountSummary(): Observable<ApiResponse<AccountSummaryResponse>> {
    return this.http.get<ApiResponse<AccountSummaryResponse>>(`${this.apiUrl}/account-summary`);
  }

  getRecentTransactions(): Observable<ApiResponse<TransactionResponse[]>> {
    return this.http.get<ApiResponse<TransactionResponse[]>>(`${this.txnUrl}/recent`);
  }

  getAccountStatement(fromDate: string, toDate: string): Observable<ApiResponse<TransactionResponse[]>> {
    return this.http.post<ApiResponse<TransactionResponse[]>>(`${this.txnUrl}/statement`, { fromDate, toDate });
  }

  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.apiUrl}/profile`);
  }

  updateProfile(payload: any): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.apiUrl}/profile`, payload);
  }
}
