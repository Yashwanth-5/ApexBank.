package com.apexbank.account.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/** Internal DTO: transaction-service asks account-service to debit/credit an account by ID. */
@Data
public class DebitCreditRequest {
    private BigDecimal amount;
}
