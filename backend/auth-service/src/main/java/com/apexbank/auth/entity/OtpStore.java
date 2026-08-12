package com.apexbank.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="otp_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String identifier;

    @Column(nullable=false)
    private String otpCode;

    @Column(nullable=false)
    private String purpose;

    @Builder.Default
    private Boolean used=false;

    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime expiresAt;
}