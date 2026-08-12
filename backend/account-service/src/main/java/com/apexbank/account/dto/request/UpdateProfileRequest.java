package com.apexbank.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank @Pattern(regexp = "^[6-9]\\d{9}$")
    private String mobileNumber;

    @Email
    private String email;

    @NotBlank private String residentialAddressLine1;
    private String residentialAddressLine2;
    private String residentialLandmark;
    @NotBlank private String residentialState;
    @NotBlank private String residentialCity;
    @NotBlank @Pattern(regexp = "^\\d{6}$") private String residentialPincode;

    @NotBlank private String permanentAddressLine1;
    private String permanentAddressLine2;
    private String permanentLandmark;
    @NotBlank private String permanentState;
    @NotBlank private String permanentCity;
    @NotBlank @Pattern(regexp = "^\\d{6}$") private String permanentPincode;

    @NotBlank private String occupationType;
}
