package com.apexbank.txn.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpiTransferRequest {
    private String toUpiId;
    private String toAccountNumber;

    @NotNull @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    private BigDecimal amount;

    private String remarks;
    @NotNull private String transactionPassword;

    @AssertTrue(message = "Provide either a UPI ID or an Account Number, not both or neither")
    private boolean isValidTarget() {
        boolean hasUpi = toUpiId != null && !toUpiId.isBlank();
        boolean hasAcc = toAccountNumber != null && !toAccountNumber.isBlank();
        return hasUpi ^ hasAcc;
    }
}
