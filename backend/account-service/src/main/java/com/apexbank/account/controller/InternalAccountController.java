package com.apexbank.account.controller;

import com.apexbank.account.dto.request.DebitCreditRequest;
import com.apexbank.account.dto.response.AccountInternalResponse;
import com.apexbank.account.service.InternalAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Service-to-service endpoints only. The API Gateway does NOT route
 * "/api/internal/**" — it's reachable only from other Eureka-registered
 * services on the internal network, keeping account balances safe from
 * direct manipulation by the public frontend.
 */
@RestController
@RequestMapping("/api/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {

    private final InternalAccountService internalAccountService;

    @GetMapping("/by-number/{accountNumber}")
    public AccountInternalResponse getByAccountNumber(@PathVariable String accountNumber) {
        return internalAccountService.getByAccountNumber(accountNumber);
    }

    @GetMapping("/{accountId}")
    public AccountInternalResponse getById(@PathVariable Long accountId) {
        return internalAccountService.getById(accountId);
    }

    @GetMapping("/by-upi/{upiId}")
    public AccountInternalResponse resolveUpiId(@PathVariable String upiId) {
        return internalAccountService.resolveUpiId(upiId);
    }

    @PostMapping("/{accountId}/debit")
    public AccountInternalResponse debit(@PathVariable Long accountId, @RequestBody DebitCreditRequest request) {
        return internalAccountService.debit(accountId, request.getAmount());
    }

    @PostMapping("/{accountId}/credit")
    public AccountInternalResponse credit(@PathVariable Long accountId, @RequestBody DebitCreditRequest request) {
        return internalAccountService.credit(accountId, request.getAmount());
    }
}
