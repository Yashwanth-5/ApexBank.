import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { AccountOpeningRequestResponse } from '../models/account-request.model';

// ── Admin-specific models ──────────────────────────────────────────────────

export interface AdminAccountResponse {
  id: number;
  accountNumber: string;
  title: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  mobileNumber: string;
  email?: string;
  aadharNumber: string;
  dateOfBirth: string;
  residentialAddressLine1: string;
  residentialAddressLine2?: string;
  residentialLandmark?: string;
  residentialState: string;
  residentialCity: string;
  residentialPincode: string;
  permanentAddressLine1: string;
  permanentAddressLine2?: string;
  permanentLandmark?: string;
  permanentState: string;
  permanentCity: string;
  permanentPincode: string;
  occupationType: string;
  accountType: string;
  balance: number;
  hasDebitCard: boolean;
  netBankingEnabled: boolean;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface AdminTransactionResponse {
  referenceId: string;
  mode: string;
  amount: number;
  direction: 'CREDIT' | 'DEBIT';
  fromAccountNumber: string;
  toAccountNumber: string;
  remarks?: string;
  status: string;
  transactionDatetime: string;
}

export interface AdminSystemStats {
  totalUsers: number;
  totalBalance: number;
  pendingRequests: number;
  approvedRequests: number;
  rejectedRequests: number;
  totalTransactions: number;
  totalTransactionVolume: number;
}

export interface AdminUpdateProfileRequest {
  mobileNumber: string;
  email?: string;
  residentialAddressLine1: string;
  residentialAddressLine2?: string;
  residentialLandmark?: string;
  residentialState: string;
  residentialCity: string;
  residentialPincode: string;
  permanentAddressLine1: string;
  permanentAddressLine2?: string;
  permanentLandmark?: string;
  permanentState: string;
  permanentCity: string;
  permanentPincode: string;
  occupationType: string;
}

export interface AdminCreditRequest {
  accountNumber: string;
  amount: number;
  remarks?: string;
}

// ── Service ────────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly base = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  // ── Account Opening Requests ───────────────────────────────────────────

  getPendingRequests(): Observable<ApiResponse<AccountOpeningRequestResponse[]>> {
    return this.http.get<ApiResponse<AccountOpeningRequestResponse[]>>(
      `${this.base}/account-requests/pending`
    );
  }

  getAllRequests(): Observable<ApiResponse<AccountOpeningRequestResponse[]>> {
    return this.http.get<ApiResponse<AccountOpeningRequestResponse[]>>(
      `${this.base}/account-requests`
    );
  }

  approveRequest(id: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      `${this.base}/account-requests/${id}/approve`, {}
    );
  }

  rejectRequest(id: number, reason: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${this.base}/account-requests/${id}/reject`, { reason }
    );
  }

  // ── Users / Accounts ───────────────────────────────────────────────────

  getAllAccounts(): Observable<ApiResponse<AdminAccountResponse[]>> {
    return this.http.get<ApiResponse<AdminAccountResponse[]>>(
      `${this.base}/accounts`
    );
  }

  getAccountByNumber(accountNumber: string): Observable<ApiResponse<AdminAccountResponse>> {
    return this.http.get<ApiResponse<AdminAccountResponse>>(
      `${this.base}/accounts/${accountNumber}`
    );
  }

  updateAccount(accountNumber: string, dto: AdminUpdateProfileRequest): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      `${this.base}/accounts/${accountNumber}`, dto
    );
  }

  // ── Credit (deposit) ───────────────────────────────────────────────────

  creditAccount(dto: AdminCreditRequest): Observable<ApiResponse<AdminAccountResponse>> {
    return this.http.post<ApiResponse<AdminAccountResponse>>(
      `${this.base}/accounts/credit`, dto
    );
  }

  // ── Transactions ───────────────────────────────────────────────────────

  getAllTransactions(): Observable<ApiResponse<AdminTransactionResponse[]>> {
    return this.http.get<ApiResponse<AdminTransactionResponse[]>>(
      `${this.base}/transactions`
    );
  }

  // ── System Stats ───────────────────────────────────────────────────────

  getSystemStats(): Observable<ApiResponse<AdminSystemStats>> {
    return this.http.get<ApiResponse<AdminSystemStats>>(
      `${this.base}/stats`
    );
  }
}
