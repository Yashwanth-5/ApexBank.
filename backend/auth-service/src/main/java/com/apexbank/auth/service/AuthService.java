package com.apexbank.auth.service;

import com.apexbank.auth.client.AccountClient;
import com.apexbank.auth.dto.request.*;
import com.apexbank.auth.dto.response.LoginResponse;
import com.apexbank.auth.entity.User;
import com.apexbank.auth.exception.*;
import com.apexbank.auth.repository.UserRepository;
import com.apexbank.auth.security.CustomUserDetails;
import com.apexbank.auth.security.JwtTokenProvider;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final AccountClient accountClient;

    @Value("${apexbank.security.max-login-attempts:3}")
    private int maxLoginAttempts;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid User ID or Password"));

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new AccountLockedException(
                    "Account locked due to multiple failed login attempts. Please reset your password.");
        }

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getLoginPasswordHash());

        if (!passwordMatches) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid User ID or Password");
        }

        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid User ID or Password");
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .accountNumber(user.getAccountNumber())
                .fullName(user.getFullName())
                .role(user.getRole())
                .expiresInMs(jwtTokenProvider.getExpirationMs())
                .build();
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxLoginAttempts) {
            user.setAccountLocked(true);
        }
        userRepository.save(user);
    }

    @Transactional
    public String sendOtp(SendOtpRequest request) {
        switch (request.getPurpose()) {
            case "REGISTER", "FORGOT_USER_ID" -> {
                try {
                    accountClient.getAccountByNumber(request.getIdentifier());
                } catch (FeignException.NotFound ex) {
                    throw new ResourceNotFoundException("Account number not found");
                }
            }
            case "FORGOT_PASSWORD" -> userRepository.findByUserId(request.getIdentifier())
                    .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));
            default -> throw new IllegalArgumentException("Invalid OTP purpose");
        }

        return otpService.generateOtp(request.getIdentifier(), request.getPurpose());
    }

    @Transactional
    public void registerForInternetBanking(RegisterInternetBankingRequest request) {
        if (!request.getLoginPassword().equals(request.getConfirmLoginPassword())) {
            throw new IllegalArgumentException("Login password and confirm password do not match");
        }
        if (!request.getTransactionPassword().equals(request.getConfirmTransactionPassword())) {
            throw new IllegalArgumentException("Transaction password and confirm password do not match");
        }

        AccountClient.AccountInternalDto account;
        try {
            account = accountClient.getAccountByNumber(request.getAccountNumber());
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Account number not found");
        }

        if (!"ACTIVE".equals(account.status())) {
            throw new IllegalStateException("Account is not active. Contact bank support.");
        }

        if (userRepository.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            throw new DuplicateResourceException("Internet banking is already registered for this account");
        }

        otpService.validateOtp(request.getAccountNumber(), "REGISTER", request.getOtp());

        String generatedUserId = generateUserId(account.firstName(), account.lastName());

        User user = User.builder()
                .userId(generatedUserId)
                .accountId(account.id())
                .accountNumber(account.accountNumber())
                .fullName(account.fullName())
                .loginPasswordHash(passwordEncoder.encode(request.getLoginPassword()))
                .transactionPasswordHash(passwordEncoder.encode(request.getTransactionPassword()))
                .role("CUSTOMER")
                .failedLoginAttempts(0)
                .accountLocked(false)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public String forgotUserId(ForgotUserIdRequest request) {
        otpService.validateOtp(request.getAccountNumber(), "FORGOT_USER_ID", request.getOtp());

        User user = userRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("No internet banking user found for this account"));

        return user.getUserId();
    }

    @Transactional
    public void forgotPasswordValidateOtp(ForgotPasswordRequest request) {
        userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));

        otpService.validateOtp(request.getUserId(), "FORGOT_PASSWORD", request.getOtp());
    }

    @Transactional
    public void setNewPassword(SetNewPasswordRequest request) {
        if (!request.getNewLoginPassword().equals(request.getConfirmLoginPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));

        user.setLoginPasswordHash(passwordEncoder.encode(request.getNewLoginPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        if (!request.getNewLoginPassword().equals(request.getConfirmNewLoginPassword())) {
            throw new IllegalArgumentException("New login password and confirm password do not match");
        }
        if (!request.getNewTransactionPassword().equals(request.getConfirmNewTransactionPassword())) {
            throw new IllegalArgumentException("New transaction password and confirm password do not match");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentLoginPassword(), user.getLoginPasswordHash())) {
            throw new InvalidCredentialsException("Current login password is incorrect");
        }
        if (!passwordEncoder.matches(request.getCurrentTransactionPassword(), user.getTransactionPasswordHash())) {
            throw new InvalidCredentialsException("Current transaction password is incorrect");
        }

        user.setLoginPasswordHash(passwordEncoder.encode(request.getNewLoginPassword()));
        user.setTransactionPasswordHash(passwordEncoder.encode(request.getNewTransactionPassword()));
        userRepository.save(user);
    }

    /** Used internally (via Feign) by transaction-service to verify the transaction password before a transfer. */
    public boolean verifyTransactionPassword(String userId, String rawTransactionPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return passwordEncoder.matches(rawTransactionPassword, user.getTransactionPasswordHash());
    }

    private String generateUserId(String firstName, String lastName) {
        String base = (firstName + "." + lastName).toLowerCase().replaceAll("[^a-z.]", "");
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUserId(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }
}
