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
public class AdminSystemStats {
    private long totalUsers;
    private BigDecimal totalBalance;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    // Transaction stats will be populated by transaction-service
    private long totalTransactions;
    private BigDecimal totalTransactionVolume;
}