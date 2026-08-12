package com.apexbank.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Internal DTO returned by account-service's /api/internal/** endpoints for other services. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInternalResponse {
    private Long id;
    private String accountNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String status;
    private BigDecimal balance;
}
