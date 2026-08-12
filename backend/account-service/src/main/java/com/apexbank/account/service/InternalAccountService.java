package com.apexbank.account.service;

import com.apexbank.account.dto.response.AccountInternalResponse;
import com.apexbank.account.entity.Account;
import com.apexbank.account.entity.UpiId;
import com.apexbank.account.exception.InsufficientBalanceException;
import com.apexbank.account.exception.ResourceNotFoundException;
import com.apexbank.account.repository.AccountRepository;
import com.apexbank.account.repository.UpiIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Powers the /api/internal/accounts/** endpoints consumed by auth-service
 * (account lookups) and transaction-service (balance debit/credit, UPI
 * resolution) via Feign. These endpoints are not meant to be reachable
 * from the public internet — in production they'd sit behind network
 * policies so only other services on the private network can call them
 * (the API Gateway does not route /api/internal/** at all).
 */
@Service
@RequiredArgsConstructor
public class InternalAccountService {

    private final AccountRepository accountRepository;
    private final UpiIdRepository upiIdRepository;

    public AccountInternalResponse getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account number not found"));
        return toInternalResponse(account);
    }

    public AccountInternalResponse getById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return toInternalResponse(account);
    }

    public AccountInternalResponse resolveUpiId(String upiId) {
        UpiId upi = upiIdRepository.findByUpiId(upiId)
                .orElseThrow(() -> new ResourceNotFoundException("UPI ID not found"));

        if (!Boolean.TRUE.equals(upi.getActive())) {
            throw new IllegalStateException("This UPI ID is inactive");
        }

        return getById(upi.getAccountId());
    }

    @Transactional
    public AccountInternalResponse debit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to complete this transfer");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return toInternalResponse(account);
    }

    @Transactional
    public AccountInternalResponse credit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return toInternalResponse(account);
    }

    private AccountInternalResponse toInternalResponse(Account account) {
        return AccountInternalResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .fullName(account.getFullName())
                .status(account.getStatus())
                .balance(account.getBalance())
                .build();
    }
}
