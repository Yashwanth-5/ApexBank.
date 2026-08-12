package com.apexbank.txn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Note: fromAccountId/toAccountId/fromAccountNumber/toAccountNumber are
 * stored as plain fields (not JPA relations) since Account lives in a
 * different service's database in this microservices split. Account
 * numbers are denormalized here at transfer time purely for fast, simple
 * statement rendering without a network call per row.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_id", nullable = false, unique = true, length = 30)
    private String referenceId;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;
    @Column(name = "from_account_number", nullable = false, length = 20)
    private String fromAccountNumber;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;
    @Column(name = "to_account_number", nullable = false, length = 20)
    private String toAccountNumber;

    @Column(nullable = false, length = 10)
    private String mode; // NEFT, UPI

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String remarks;

    @Column(name = "upi_id_used", length = 50)
    private String upiIdUsed;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "SUCCESS";

    @Column(name = "transaction_datetime", nullable = false)
    @Builder.Default
    private LocalDateTime transactionDatetime = LocalDateTime.now();
}
