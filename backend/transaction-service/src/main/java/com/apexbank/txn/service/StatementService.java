package com.apexbank.txn.service;

import com.apexbank.txn.dto.request.StatementFilterRequest;
import com.apexbank.txn.dto.response.TransactionResponse;
import com.apexbank.txn.entity.Transaction;
import com.apexbank.txn.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> getRecentTransactions(Long accountId) {
        List<Transaction> recent = transactionRepository
                .findTop5ByFromAccountIdOrToAccountIdOrderByTransactionDatetimeDesc(accountId, accountId);
        return mapTransactions(recent, accountId);
    }

    public List<TransactionResponse> getStatement(Long accountId, StatementFilterRequest filter) {
        LocalDateTime start = filter.getFromDate().atStartOfDay();
        LocalDateTime end = filter.getToDate().atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionRepository
                .findByFromAccountIdAndTransactionDatetimeBetweenOrToAccountIdAndTransactionDatetimeBetween(
                        accountId, start, end, accountId, start, end);

        return mapTransactions(transactions, accountId);
    }

    private List<TransactionResponse> mapTransactions(List<Transaction> transactions, Long accountId) {
        return transactions.stream()
                .map(t -> TransactionResponse.builder()
                        .referenceId(t.getReferenceId())
                        .mode(t.getMode())
                        .amount(t.getAmount())
                        .direction(t.getFromAccountId().equals(accountId) ? "DEBIT" : "CREDIT")
                        .fromAccountNumber(t.getFromAccountNumber())
                        .toAccountNumber(t.getToAccountNumber())
                        .remarks(t.getRemarks())
                        .status(t.getStatus())
                        .transactionDatetime(t.getTransactionDatetime())
                        .build())
                .collect(Collectors.toList());
    }
}
