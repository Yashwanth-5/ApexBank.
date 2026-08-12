package com.apexbank.txn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/api/internal/accounts/by-number/{accountNumber}")
    AccountInternalDto getByAccountNumber(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/api/internal/accounts/{accountId}")
    AccountInternalDto getById(@PathVariable("accountId") Long accountId);

    @GetMapping("/api/internal/accounts/by-upi/{upiId}")
    AccountInternalDto resolveUpiId(@PathVariable("upiId") String upiId);

    @PostMapping("/api/internal/accounts/{accountId}/debit")
    AccountInternalDto debit(@PathVariable("accountId") Long accountId, @RequestBody DebitCreditRequest request);

    @PostMapping("/api/internal/accounts/{accountId}/credit")
    AccountInternalDto credit(@PathVariable("accountId") Long accountId, @RequestBody DebitCreditRequest request);

    record AccountInternalDto(
            Long id, String accountNumber, String firstName, String lastName,
            String fullName, String status, BigDecimal balance
    ) {}

    record DebitCreditRequest(BigDecimal amount) {}
}
