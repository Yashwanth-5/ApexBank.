export interface TransactionResponse {
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

export interface DashboardResponse {
  accountNumber: string;
  fullName: string;
  accountType: string;
  balance: number;
  upiId?: string;
}

export interface AccountSummaryResponse {
  accountNumber: string;
  balance: number;
}

export interface TransferSuccessResponse {
  referenceId: string;
  mode: string;
  amount: number;
  fromAccountNumber: string;
  toAccountNumber: string;
  toUpiId?: string;
  remarks?: string;
  dateTime: string;
}

export interface PayeeResponse {
  id: number;
  payeeName: string;
  payeeAccountNumber: string;
  nickname?: string;
}
