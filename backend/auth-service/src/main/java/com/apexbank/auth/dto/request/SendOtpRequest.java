package com.apexbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {
    @NotBlank(message = "Identifier (account number or user id) is required")
    private String identifier;

    @NotBlank(message = "Purpose is required")
    private String purpose; // REGISTER, FORGOT_USER_ID, FORGOT_PASSWORD
}
