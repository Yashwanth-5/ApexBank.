package com.apexbank.account.controller;

import com.apexbank.account.dto.request.AccountOpeningRequestDto;
import com.apexbank.account.dto.response.AccountOpeningRequestResponse;
import com.apexbank.account.dto.response.ApiResponse;
import com.apexbank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/open")
    public ResponseEntity<ApiResponse<AccountOpeningRequestResponse>> openAccount(
            @Valid @RequestBody AccountOpeningRequestDto dto) {
        AccountOpeningRequestResponse response = accountService.submitAccountOpeningRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Account opening request submitted successfully. It is pending admin approval.", response));
    }

    @GetMapping("/open/{requestId}/status")
    public ResponseEntity<ApiResponse<AccountOpeningRequestResponse>> getRequestStatus(@PathVariable Long requestId) {
        return ResponseEntity.ok(ApiResponse.success("Request status fetched", accountService.getRequestById(requestId)));
    }
}
