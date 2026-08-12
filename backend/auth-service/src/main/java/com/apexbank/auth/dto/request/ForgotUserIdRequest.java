package com.apexbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotUserIdRequest {
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "OTP is required")
    private String otp;
}
