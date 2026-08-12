export interface LoginRequest {
  userId: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  accountNumber: string;
  fullName: string;
  role: string;
  expiresInMs: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  validationErrors?: { [key: string]: string };
}

export interface UserProfile {
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
  upiId?: string;
  hasDebitCard: boolean;
  netBankingEnabled: boolean;
}
