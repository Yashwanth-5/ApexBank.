package com.apexbank.auth.service;

import com.apexbank.auth.entity.OtpStore;
import com.apexbank.auth.exception.InvalidOtpException;
import com.apexbank.auth.repository.OtpStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpStoreRepository otpStoreRepository;

    @Value("${apexbank.otp.length:6}")
    private int otpLength;

    @Value("${apexbank.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${apexbank.otp.dev-mode:true}")
    private boolean devMode;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public String generateOtp(String identifier, String purpose) {
        String otp = generateNumericOtp();

        OtpStore otpStore = OtpStore.builder()
                .identifier(identifier)
                .otpCode(otp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .build();

        otpStoreRepository.save(otpStore);
        return otp;
    }

    @Transactional
    public void validateOtp(String identifier, String purpose, String providedOtp) {
        OtpStore otpStore = otpStoreRepository
                .findTopByIdentifierAndPurposeAndUsedFalseOrderByCreatedAtDesc(identifier, purpose)
                .orElseThrow(() -> new InvalidOtpException("No OTP request found. Please request a new OTP."));

        if (otpStore.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired. Please request a new one.");
        }

        if (!otpStore.getOtpCode().equals(providedOtp)) {
            throw new InvalidOtpException("Invalid OTP provided.");
        }

        otpStore.setUsed(true);
        otpStoreRepository.save(otpStore);
    }

    public boolean isDevMode() {
        return devMode;
    }

    private String generateNumericOtp() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
