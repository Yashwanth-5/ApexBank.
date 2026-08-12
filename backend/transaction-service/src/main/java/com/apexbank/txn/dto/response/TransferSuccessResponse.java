package com.apexbank.txn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferSuccessResponse {
    private String referenceId;
    private String mode;
    private BigDecimal amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String toUpiId;
    private String remarks;
    private LocalDateTime dateTime;
}
