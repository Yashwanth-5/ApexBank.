package com.apexbank.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectRequestDto {
    @NotBlank(message = "Rejection reason is required")
    private String reason;
}
