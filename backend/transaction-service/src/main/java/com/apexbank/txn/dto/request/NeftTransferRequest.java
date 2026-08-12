package com.apexbank.txn.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NeftTransferRequest {
    @NotBlank private String toAccountNumber;
    @NotNull @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    private BigDecimal amount;
    private String remarks;
    @NotBlank private String transactionPassword;
}
