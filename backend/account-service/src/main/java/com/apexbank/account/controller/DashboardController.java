package com.apexbank.account.controller;

import com.apexbank.account.dto.request.UpdateProfileRequest;
import com.apexbank.account.dto.response.*;
import com.apexbank.account.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(@RequestHeader("X-Auth-Account-Id") Long accountId) {
        return ApiResponse.success("Dashboard fetched", dashboardService.getDashboard(accountId));
    }

    @GetMapping("/account-summary")
    public ApiResponse<AccountSummaryResponse> getAccountSummary(@RequestHeader("X-Auth-Account-Id") Long accountId) {
        return ApiResponse.success("Account summary fetched", dashboardService.getAccountSummary(accountId));
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(@RequestHeader("X-Auth-Account-Id") Long accountId) {
        return ApiResponse.success("Profile fetched", dashboardService.getProfile(accountId));
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(
            @RequestHeader("X-Auth-Account-Id") Long accountId, @Valid @RequestBody UpdateProfileRequest request) {
        dashboardService.updateProfile(accountId, request);
        return ApiResponse.success("Profile updated successfully");
    }
}
