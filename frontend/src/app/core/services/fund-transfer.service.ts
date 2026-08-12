import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { PayeeResponse, TransferSuccessResponse } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class FundTransferService {
  private readonly apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  addPayee(payload: any): Observable<ApiResponse<PayeeResponse>> {
    return this.http.post<ApiResponse<PayeeResponse>>(`${this.apiUrl}/payees`, payload);
  }

  getPayees(): Observable<ApiResponse<PayeeResponse[]>> {
    return this.http.get<ApiResponse<PayeeResponse[]>>(`${this.apiUrl}/payees`);
  }

  deletePayee(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/payees/${id}`);
  }

  transferNeft(payload: any): Observable<ApiResponse<TransferSuccessResponse>> {
    return this.http.post<ApiResponse<TransferSuccessResponse>>(`${this.apiUrl}/fund-transfer/neft`, payload);
  }

  transferUpi(payload: any): Observable<ApiResponse<TransferSuccessResponse>> {
    return this.http.post<ApiResponse<TransferSuccessResponse>>(`${this.apiUrl}/fund-transfer/upi`, payload);
  }

  getMyUpiId(): Observable<ApiResponse<{ upiId: string; active: boolean }>> {
    return this.http.get<ApiResponse<{ upiId: string; active: boolean }>>(`${this.apiUrl}/dashboard/upi-id`);
  }

  setUpiId(customPrefix: string): Observable<ApiResponse<{ upiId: string; active: boolean }>> {
    return this.http.post<ApiResponse<{ upiId: string; active: boolean }>>(`${this.apiUrl}/dashboard/upi-id`, { customPrefix });
  }
}
