package com.apexbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetNewPasswordRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "New login password is required")
    @Size(min = 8, message = "Login password must be at least 8 characters")
    private String newLoginPassword;

    @NotBlank(message = "Confirm login password is required")
    private String confirmLoginPassword;
}
