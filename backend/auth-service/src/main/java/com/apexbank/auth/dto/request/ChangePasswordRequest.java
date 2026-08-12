package com.apexbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Current login password is required")
    private String currentLoginPassword;

    @NotBlank(message = "New login password is required")
    @Size(min = 8, message = "Login password must be at least 8 characters")
    private String newLoginPassword;

    @NotBlank(message = "Confirm new login password is required")
    private String confirmNewLoginPassword;

    @NotBlank(message = "Current transaction password is required")
    private String currentTransactionPassword;

    @NotBlank(message = "New transaction password is required")
    @Size(min = 6, message = "Transaction password must be at least 6 characters")
    private String newTransactionPassword;

    @NotBlank(message = "Confirm new transaction password is required")
    private String confirmNewTransactionPassword;
}
