package com.apexbank.account.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountOpeningRequestDto {
    @NotBlank private String title;
    @NotBlank private String firstName;
    private String middleName;
    @NotBlank private String lastName;
    @NotBlank private String fatherName;

    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    private String mobileNumber;

    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be exactly 12 digits")
    private String aadharNumber;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank private String residentialAddressLine1;
    private String residentialAddressLine2;
    private String residentialLandmark;
    @NotBlank private String residentialState;
    @NotBlank private String residentialCity;
    @NotBlank @Pattern(regexp = "^\\d{6}$") private String residentialPincode;

    private boolean permanentSameAsResidential;
    @NotBlank private String permanentAddressLine1;
    private String permanentAddressLine2;
    private String permanentLandmark;
    @NotBlank private String permanentState;
    @NotBlank private String permanentCity;
    @NotBlank @Pattern(regexp = "^\\d{6}$") private String permanentPincode;

    @NotBlank private String occupationType;
    @NotBlank private String sourceOfIncome;
    @NotBlank private String grossAnnualIncome;

    @NotNull private Boolean wantsDebitCard;
    private boolean optForNetBanking;

    @AssertTrue(message = "You must agree to the terms and conditions")
    private boolean agreeTerms;
}
