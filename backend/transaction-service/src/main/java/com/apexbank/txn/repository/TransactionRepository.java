package com.apexbank.txn.repository;

import com.apexbank.txn.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByReferenceId(String referenceId);

    List<Transaction> findByFromAccountIdOrToAccountIdOrderByTransactionDatetimeDesc(
            Long fromAccountId, Long toAccountId);

    List<Transaction> findTop5ByFromAccountIdOrToAccountIdOrderByTransactionDatetimeDesc(
            Long fromAccountId, Long toAccountId);

    List<Transaction> findByFromAccountIdAndTransactionDatetimeBetweenOrToAccountIdAndTransactionDatetimeBetween(
            Long fromAccountId, LocalDateTime start1, LocalDateTime end1,
            Long toAccountId, LocalDateTime start2, LocalDateTime end2);
    
    // Explicit query to ensure it works - backup method
    @Query("SELECT t FROM Transaction t ORDER BY t.transactionDatetime DESC")
    List<Transaction> findAllOrderByDateDesc();
    
    // Alternative method using method naming convention
    List<Transaction> findAllByOrderByTransactionDatetimeDesc();
    
    // Simple count method for debugging
    @Query("SELECT COUNT(t) FROM Transaction t")
    long countAllTransactions();
}
