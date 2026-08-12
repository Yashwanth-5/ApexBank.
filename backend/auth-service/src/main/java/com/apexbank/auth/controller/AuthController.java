package com.apexbank.auth.controller;

import com.apexbank.auth.dto.request.*;
import com.apexbank.auth.dto.response.ApiResponse;
import com.apexbank.auth.dto.response.LoginResponse;
import com.apexbank.auth.dto.response.OtpResponse;
import com.apexbank.auth.service.AuthService;
import com.apexbank.auth.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        String otp = authService.sendOtp(request);
        OtpResponse response = OtpResponse.builder()
                .message("OTP sent to your registered mobile number")
                .devOtp(otpService.isDevMode() ? otp : null)
                .build();
        return ResponseEntity.ok(ApiResponse.success("OTP generated successfully", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterInternetBankingRequest request) {
        authService.registerForInternetBanking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Internet banking registration successful. You may now log in."));
    }

    @PostMapping("/forgot-user-id")
    public ResponseEntity<ApiResponse<String>> forgotUserId(@Valid @RequestBody ForgotUserIdRequest request) {
        String userId = authService.forgotUserId(request);
        return ResponseEntity.ok(ApiResponse.success("User ID retrieved. It has also been sent to your registered email.", userId));
    }

    @PostMapping("/forgot-password/validate-otp")
    public ResponseEntity<ApiResponse<Void>> forgotPasswordValidateOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPasswordValidateOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified. You may now set a new password."));
    }

    @PostMapping("/set-new-password")
    public ResponseEntity<ApiResponse<Void>> setNewPassword(@Valid @RequestBody SetNewPasswordRequest request) {
        authService.setNewPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful. Please log in with your new password."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("X-Auth-User-Id") String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    /** Internal endpoint — called by transaction-service via Feign to verify the transaction password. */
    @PostMapping("/internal/verify-transaction-password")
    public ResponseEntity<Boolean> verifyTransactionPassword(
            @RequestParam String userId, @RequestParam String password) {
        return ResponseEntity.ok(authService.verifyTransactionPassword(userId, password));
    }
}
