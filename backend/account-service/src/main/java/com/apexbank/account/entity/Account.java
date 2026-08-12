package com.apexbank.account.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "request_id")
    private Long requestId;

    @Column(nullable = false, length = 10)
    private String title;
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    @Column(name = "middle_name", length = 50)
    private String middleName;
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;
    @Column(length = 100)
    private String email;
    @Column(name = "aadhar_number", nullable = false, length = 12)
    private String aadharNumber;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "residential_address_line1", nullable = false, length = 150)
    private String residentialAddressLine1;
    @Column(name = "residential_address_line2", length = 150)
    private String residentialAddressLine2;
    @Column(name = "residential_landmark", length = 100)
    private String residentialLandmark;
    @Column(name = "residential_state", nullable = false, length = 50)
    private String residentialState;
    @Column(name = "residential_city", nullable = false, length = 50)
    private String residentialCity;
    @Column(name = "residential_pincode", nullable = false, length = 10)
    private String residentialPincode;

    @Column(name = "permanent_address_line1", nullable = false, length = 150)
    private String permanentAddressLine1;
    @Column(name = "permanent_address_line2", length = 150)
    private String permanentAddressLine2;
    @Column(name = "permanent_landmark", length = 100)
    private String permanentLandmark;
    @Column(name = "permanent_state", nullable = false, length = 50)
    private String permanentState;
    @Column(name = "permanent_city", nullable = false, length = 50)
    private String permanentCity;
    @Column(name = "permanent_pincode", nullable = false, length = 10)
    private String permanentPincode;

    @Column(name = "occupation_type", nullable = false, length = 50)
    private String occupationType;

    @Column(name = "account_type", nullable = false, length = 20)
    @Builder.Default
    private String accountType = "SAVINGS";

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "has_debit_card")
    private Boolean hasDebitCard;
    @Column(name = "net_banking_enabled")
    private Boolean netBankingEnabled;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, CLOSED

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return middleName != null && !middleName.isBlank()
                ? String.format("%s %s %s", firstName, middleName, lastName)
                : String.format("%s %s", firstName, lastName);
    }
}
