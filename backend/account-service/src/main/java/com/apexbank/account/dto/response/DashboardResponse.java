package com.apexbank.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private String accountNumber;
    private String fullName;
    private String accountType;
    private BigDecimal balance;
    private String upiId;
}
