package com.apexbank.account.controller;

import com.apexbank.account.dto.request.AdminCreditRequest;
import com.apexbank.account.dto.request.RejectRequestDto;
import com.apexbank.account.dto.request.UpdateProfileRequest;
import com.apexbank.account.dto.response.*;
import com.apexbank.account.service.AccountService;
import com.apexbank.account.service.AdminAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only endpoints. The API Gateway forwards the caller's roles in the
 * X-Auth-Roles header (resolved from their JWT); this controller checks
 * that header directly since Spring Security isn't part of this service.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountService accountService;
    private final AdminAccountService adminAccountService;

    @GetMapping("/account-requests/pending")
    public ResponseEntity<ApiResponse<List<AccountOpeningRequestResponse>>> getPendingRequests(
            @RequestHeader("X-Auth-Roles") String roles) {
        requireAdmin(roles);
        return ResponseEntity.ok(ApiResponse.success("Pending requests fetched", accountService.getPendingRequests()));
    }

    @GetMapping("/account-requests")
    public ResponseEntity<ApiResponse<List<AccountOpeningRequestResponse>>> getAllRequests(
            @RequestHeader("X-Auth-Roles") String roles) {
        requireAdmin(roles);
        return ResponseEntity.ok(ApiResponse.success("All requests fetched", accountService.getAllRequests()));
    }

    @GetMapping("/account-requests/{id}")
    public ResponseEntity<ApiResponse<AccountOpeningRequestResponse>> getRequestById(
            @RequestHeader("X-Auth-Roles") String roles, @PathVariable Long id) {
        requireAdmin(roles);
        return ResponseEntity.ok(ApiResponse.success("Request fetched", accountService.getRequestById(id)));
    }

    @PostMapping("/account-requests/{id}/approve")
    public ResponseEntity<ApiResponse<AccountApprovedResponse>> approveRequest(
            @RequestHeader("X-Auth-Roles") String roles, @PathVariable Long id) {
        requireAdmin(roles);
        AccountApprovedResponse response = accountService.approveRequest(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Request approved. Account " + response.getAccountNumber() + " has been activated.", response));
    }

    @PostMapping("/account-requests/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectRequest(
            @RequestHeader("X-Auth-Roles") String roles, @PathVariable Long id, @Valid @RequestBody RejectRequestDto dto) {
        requireAdmin(roles);
        accountService.rejectRequest(id, dto.getReason());
        return ResponseEntity.ok(ApiResponse.success("Request rejected. The user must re-apply."));
    }

    // ── Account Management ──────────────────────────────────────────────────

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AdminAccountResponse>>> getAllAccounts(
            @RequestHeader("X-Auth-Roles") String roles) {
        requireAdmin(roles);
        return ResponseEntity.ok(ApiResponse.success("All accounts fetched", adminAccountService.getAllAccounts()));
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> getAccountByNumber(
            @RequestHeader("X-Auth-Roles") String roles, @PathVariable String accountNumber) {
        requireAdmin(roles);
        return ResponseEntity.ok(ApiResponse.success("Account fetched", adminAccountService.getAccountByNumber(accountNumber)));
    }

    @PutMapping("/accounts/{accountNumber}")
    public ResponseEntity<ApiResponse<Void>> updateAccount(
            @RequestHeader("X-Auth-Roles") String roles, 
            @PathVariable String accountNumber, 
            @Valid @RequestBody UpdateProfileRequest request) {
        requireAdmin(roles);
        adminAccountService.updateAccount(accountNumber, request);
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully"));
    }

    @PostMapping("/accounts/credit")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> creditAccount(
            @RequestHeader("X-Auth-Roles") String roles, 
            @Valid @RequestBody AdminCreditRequest request) {
        requireAdmin(roles);
        AdminAccountResponse response = adminAccountService.creditAccount(request);
        return ResponseEntity.ok(ApiResponse.success("Account credited successfully", response));
    }

    // ── System Stats ────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminSystemStats>> getSystemStats(
            @RequestHeader("X-Auth-Roles") String roles) {
        requireAdmin(roles);
        return ResponseEntity.ok(ApiResponse.success("System stats fetched", adminAccountService.getSystemStats()));
    }

    private void requireAdmin(String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }
}
