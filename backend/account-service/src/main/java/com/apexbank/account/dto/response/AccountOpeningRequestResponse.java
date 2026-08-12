package com.apexbank.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOpeningRequestResponse {
    private Long id;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fatherName;
    private String mobileNumber;
    private String email;
    private String aadharNumber;
    private LocalDate dateOfBirth;
    private String residentialAddressLine1;
    private String residentialCity;
    private String residentialState;
    private String residentialPincode;
    private String permanentAddressLine1;
    private String permanentCity;
    private String permanentState;
    private String permanentPincode;
    private String occupationType;
    private String sourceOfIncome;
    private String grossAnnualIncome;
    private Boolean wantsDebitCard;
    private Boolean optForNetBanking;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
