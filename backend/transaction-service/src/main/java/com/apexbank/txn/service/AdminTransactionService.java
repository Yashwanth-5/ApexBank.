package com.apexbank.txn.service;

import com.apexbank.txn.dto.response.TransactionResponse;
import com.apexbank.txn.entity.Transaction;
import com.apexbank.txn.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> getAllTransactions() {
        try {
            log.debug("Fetching all transactions from database");
            
            // Fetch all transactions, ordered by most recent first
            List<Transaction> transactions = transactionRepository.findAll(
                Sort.by(Sort.Direction.DESC, "transactionDatetime")
            );
            
            log.debug("Found {} transactions in database", transactions.size());
            
            // Handle empty result gracefully
            if (transactions.isEmpty()) {
                log.info("No transactions found in database");
                return Collections.emptyList();
            }
            
            // Map to response DTOs
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            log.debug("Successfully mapped {} transactions to response DTOs", responses.size());
            
            return responses;
            
        } catch (Exception e) {
            log.error("Error in getAllTransactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch transactions from database", e);
        }
    }

    public long getTransactionCount() {
        try {
            log.debug("Fetching transaction count from database");
            
            long count = transactionRepository.countAllTransactions();
            
            log.debug("Found {} transactions in database", count);
            
            return count;
            
        } catch (Exception e) {
            log.error("Error in getTransactionCount: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get transaction count from database", e);
        }
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        try {
            return TransactionResponse.builder()
                    .referenceId(transaction.getReferenceId())
                    .mode(transaction.getMode())
                    .amount(transaction.getAmount())
                    .direction("TRANSFER") // Admin sees all as transfers
                    .fromAccountNumber(transaction.getFromAccountNumber())
                    .toAccountNumber(transaction.getToAccountNumber())
                    .remarks(transaction.getRemarks())
                    .status(transaction.getStatus())
                    .transactionDatetime(transaction.getTransactionDatetime())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping transaction {} to response: {}", 
                transaction != null ? transaction.getReferenceId() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to map transaction", e);
        }
    }
}