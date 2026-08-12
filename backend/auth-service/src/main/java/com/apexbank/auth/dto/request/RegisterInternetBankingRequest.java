package com.apexbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterInternetBankingRequest {
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Login password is required")
    @Size(min = 8, message = "Login password must be at least 8 characters")
    private String loginPassword;

    @NotBlank(message = "Confirm login password is required")
    private String confirmLoginPassword;

    @NotBlank(message = "Transaction password is required")
    @Size(min = 6, message = "Transaction password must be at least 6 characters")
    private String transactionPassword;

    @NotBlank(message = "Confirm transaction password is required")
    private String confirmTransactionPassword;

    @NotBlank(message = "OTP is required")
    private String otp;
}
