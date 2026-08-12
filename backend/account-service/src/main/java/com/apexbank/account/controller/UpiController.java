package com.apexbank.account.controller;

import com.apexbank.account.dto.request.SetUpiIdRequest;
import com.apexbank.account.dto.response.ApiResponse;
import com.apexbank.account.dto.response.UpiIdResponse;
import com.apexbank.account.service.UpiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/upi-id")
@RequiredArgsConstructor
public class UpiController {

    private final UpiService upiService;

    @GetMapping
    public ApiResponse<UpiIdResponse> getMyUpiId(@RequestHeader("X-Auth-Account-Id") Long accountId) {
        return ApiResponse.success("UPI ID fetched", upiService.getMyUpiId(accountId));
    }

    @PostMapping
    public ApiResponse<UpiIdResponse> setUpiId(
            @RequestHeader("X-Auth-Account-Id") Long accountId, @Valid @RequestBody SetUpiIdRequest request) {
        return ApiResponse.success("UPI ID set successfully", upiService.setUpiId(accountId, request));
    }
}
