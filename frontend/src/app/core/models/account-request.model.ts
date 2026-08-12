export interface AccountOpeningRequestDto {
  title: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  fatherName: string;
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
  permanentSameAsResidential: boolean;
  permanentAddressLine1: string;
  permanentAddressLine2?: string;
  permanentLandmark?: string;
  permanentState: string;
  permanentCity: string;
  permanentPincode: string;
  occupationType: string;
  sourceOfIncome: string;
  grossAnnualIncome: string;
  wantsDebitCard: boolean;
  optForNetBanking: boolean;
  agreeTerms: boolean;
}

export interface AccountOpeningRequestResponse {
  id: number;
  title: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  fatherName: string;
  mobileNumber: string;
  email?: string;
  aadharNumber: string;
  dateOfBirth: string;
  residentialAddressLine1: string;
  residentialState: string;
  residentialCity: string;
  residentialPincode: string;
  permanentAddressLine1: string;
  permanentState: string;
  permanentCity: string;
  permanentPincode: string;
  occupationType: string;
  sourceOfIncome: string;
  grossAnnualIncome: string;
  wantsDebitCard: boolean;
  optForNetBanking: boolean;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  rejectionReason?: string;
  createdAt: string;
}
