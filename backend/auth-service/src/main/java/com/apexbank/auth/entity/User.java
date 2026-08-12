package com.apexbank.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String loginPasswordHash;

    @Column(nullable = false)
    private String transactionPasswordHash;

    @Column(nullable =false)
    private String fullName;

    @Column(nullable=false,unique=true)
    private String accountNumber;

    private String email;

    private String mobileNumber;

    @Builder.Default
    private String role="CUSTOMER";

    @Builder.Default
    private Integer failedLoginAttempts=0;

    @Builder.Default
    private Boolean accountLocked=false;

    @Builder.Default
    private Boolean enabled=true;

    private LocalDateTime lastLoginAt;

    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();
}