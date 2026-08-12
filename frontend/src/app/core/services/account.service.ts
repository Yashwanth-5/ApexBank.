import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { AccountOpeningRequestDto, AccountOpeningRequestResponse } from '../models/account-request.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly apiUrl = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  openAccount(payload: AccountOpeningRequestDto): Observable<ApiResponse<AccountOpeningRequestResponse>> {
    return this.http.post<ApiResponse<AccountOpeningRequestResponse>>(`${this.apiUrl}/open`, payload);
  }

  getRequestStatus(requestId: number): Observable<ApiResponse<AccountOpeningRequestResponse>> {
    return this.http.get<ApiResponse<AccountOpeningRequestResponse>>(`${this.apiUrl}/open/${requestId}/status`);
  }
}
