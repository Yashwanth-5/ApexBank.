package com.apexbank.txn.service;

import com.apexbank.txn.client.AccountClient;
import com.apexbank.txn.client.AccountClient.AccountInternalDto;
import com.apexbank.txn.client.AccountClient.DebitCreditRequest;
import com.apexbank.txn.client.AuthClient;
import com.apexbank.txn.dto.request.NeftTransferRequest;
import com.apexbank.txn.dto.request.UpiTransferRequest;
import com.apexbank.txn.dto.response.TransferSuccessResponse;
import com.apexbank.txn.entity.Transaction;
import com.apexbank.txn.exception.InvalidCredentialsException;
import com.apexbank.txn.exception.ResourceNotFoundException;
import com.apexbank.txn.repository.TransactionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Coordinates fund transfers across three services: auth-service (verify
 * transaction password), account-service (debit/credit balances), and this
 * service's own Transaction ledger.
 *
 * IMPORTANT — distributed transaction note: debit and credit happen as two
 * separate network calls to account-service, each in its own local DB
 * transaction there. This is NOT atomic in the same way a single-database
 * monolith transfer was. If the credit call fails after the debit already
 * succeeded, we compensate by crediting the source account back (a simple
 * saga-style compensating transaction) rather than leaving money "stuck".
 * A production system would typically use an outbox pattern + message
 * broker (Kafka/RabbitMQ) for guaranteed eventual consistency instead of
 * this synchronous best-effort compensation.
 */
@Service
@RequiredArgsConstructor
public class FundTransferService {

    private final AccountClient accountClient;
    private final AuthClient authClient;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransferSuccessResponse transferNeft(Long fromAccountId, String userId, NeftTransferRequest request) {
        verifyTransactionPassword(userId, request.getTransactionPassword());

        AccountInternalDto fromAccount = getAccountById(fromAccountId);
        AccountInternalDto toAccount = getAccountByNumber(request.getToAccountNumber());

        if (fromAccount.id().equals(toAccount.id())) {
            throw new IllegalArgumentException("Cannot transfer funds to your own account");
        }

        debitThenCredit(fromAccount.id(), toAccount.id(), request.getAmount());

        Transaction txn = Transaction.builder()
                .referenceId(ReferenceIdGenerator.generate())
                .fromAccountId(fromAccount.id())
                .fromAccountNumber(fromAccount.accountNumber())
                .toAccountId(toAccount.id())
                .toAccountNumber(toAccount.accountNumber())
                .mode("NEFT")
                .amount(request.getAmount())
                .remarks(request.getRemarks())
                .status("SUCCESS")
                .build();

        Transaction saved = transactionRepository.save(txn);
        return buildResponse(saved, null);
    }

    @Transactional
    public TransferSuccessResponse transferUpi(Long fromAccountId, String userId, UpiTransferRequest request) {
        verifyTransactionPassword(userId, request.getTransactionPassword());

        AccountInternalDto fromAccount = getAccountById(fromAccountId);

        AccountInternalDto toAccount;
        String upiIdUsed = null;

        if (request.getToUpiId() != null && !request.getToUpiId().isBlank()) {
            toAccount = resolveUpi(request.getToUpiId());
            upiIdUsed = request.getToUpiId();
        } else {
            toAccount = getAccountByNumber(request.getToAccountNumber());
        }

        if (fromAccount.id().equals(toAccount.id())) {
            throw new IllegalArgumentException("Cannot transfer funds to your own account");
        }

        debitThenCredit(fromAccount.id(), toAccount.id(), request.getAmount());

        Transaction txn = Transaction.builder()
                .referenceId(ReferenceIdGenerator.generate())
                .fromAccountId(fromAccount.id())
                .fromAccountNumber(fromAccount.accountNumber())
                .toAccountId(toAccount.id())
                .toAccountNumber(toAccount.accountNumber())
                .mode("UPI")
                .amount(request.getAmount())
                .remarks(request.getRemarks())
                .upiIdUsed(upiIdUsed)
                .status("SUCCESS")
                .build();

        Transaction saved = transactionRepository.save(txn);
        return buildResponse(saved, upiIdUsed);
    }

    /** Debit the source, then credit the destination; compensate (credit back) if the credit call fails. */
    private void debitThenCredit(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        accountClient.debit(fromAccountId, new DebitCreditRequest(amount));

        try {
            accountClient.credit(toAccountId, new DebitCreditRequest(amount));
        } catch (Exception ex) {
            // Compensating transaction: refund the source account since the credit leg failed.
            accountClient.credit(fromAccountId, new DebitCreditRequest(amount));
            throw new IllegalStateException("Transfer failed while crediting the beneficiary. Your amount has been refunded.", ex);
        }
    }

    private void verifyTransactionPassword(String userId, String rawPassword) {
        Boolean valid;
        try {
            valid = authClient.verifyTransactionPassword(userId, rawPassword);
        } catch (FeignException ex) {
            throw new ResourceNotFoundException("User not found");
        }
        if (valid == null || !valid) {
            throw new InvalidCredentialsException("Incorrect transaction password");
        }
    }

    private AccountInternalDto getAccountById(Long accountId) {
        try {
            return accountClient.getById(accountId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Account not found");
        }
    }

    private AccountInternalDto getAccountByNumber(String accountNumber) {
        try {
            return accountClient.getByAccountNumber(accountNumber);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Beneficiary account number not found");
        }
    }

    private AccountInternalDto resolveUpi(String upiId) {
        try {
            return accountClient.resolveUpiId(upiId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("UPI ID not found");
        }
    }

    private TransferSuccessResponse buildResponse(Transaction txn, String toUpiId) {
        return TransferSuccessResponse.builder()
                .referenceId(txn.getReferenceId())
                .mode(txn.getMode())
                .amount(txn.getAmount())
                .fromAccountNumber(txn.getFromAccountNumber())
                .toAccountNumber(txn.getToAccountNumber())
                .toUpiId(toUpiId)
                .remarks(txn.getRemarks())
                .dateTime(txn.getTransactionDatetime())
                .build();
    }
}
