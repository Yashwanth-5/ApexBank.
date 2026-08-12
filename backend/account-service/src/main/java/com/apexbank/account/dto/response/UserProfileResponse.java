package com.apexbank.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String accountNumber;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String aadharNumber;
    private LocalDate dateOfBirth;
    private String residentialAddressLine1;
    private String residentialAddressLine2;
    private String residentialLandmark;
    private String residentialState;
    private String residentialCity;
    private String residentialPincode;
    private String permanentAddressLine1;
    private String permanentAddressLine2;
    private String permanentLandmark;
    private String permanentState;
    private String permanentCity;
    private String permanentPincode;
    private String occupationType;
    private String upiId;
    private Boolean hasDebitCard;
    private Boolean netBankingEnabled;
}
