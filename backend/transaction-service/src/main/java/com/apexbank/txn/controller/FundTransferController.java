package com.apexbank.txn.controller;

import com.apexbank.txn.dto.request.NeftTransferRequest;
import com.apexbank.txn.dto.request.StatementFilterRequest;
import com.apexbank.txn.dto.request.UpiTransferRequest;
import com.apexbank.txn.dto.response.ApiResponse;
import com.apexbank.txn.dto.response.TransactionResponse;
import com.apexbank.txn.dto.response.TransferSuccessResponse;
import com.apexbank.txn.service.FundTransferService;
import com.apexbank.txn.service.StatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fund-transfer")
@RequiredArgsConstructor
public class FundTransferController {

    private final FundTransferService fundTransferService;
    private final StatementService statementService;

    @PostMapping("/neft")
    public ApiResponse<TransferSuccessResponse> transferNeft(
            @RequestHeader("X-Auth-Account-Id") Long accountId,
            @RequestHeader("X-Auth-User-Id") String userId,
            @Valid @RequestBody NeftTransferRequest request) {
        TransferSuccessResponse response = fundTransferService.transferNeft(accountId, userId, request);
        return ApiResponse.success("Transfer successful via NEFT", response);
    }

    @PostMapping("/upi")
    public ApiResponse<TransferSuccessResponse> transferUpi(
            @RequestHeader("X-Auth-Account-Id") Long accountId,
            @RequestHeader("X-Auth-User-Id") String userId,
            @Valid @RequestBody UpiTransferRequest request) {
        TransferSuccessResponse response = fundTransferService.transferUpi(accountId, userId, request);
        return ApiResponse.success("Transfer successful via UPI", response);
    }

    @GetMapping("/recent")
    public ApiResponse<List<TransactionResponse>> getRecentTransactions(
            @RequestHeader("X-Auth-Account-Id") Long accountId) {
        return ApiResponse.success("Recent transactions fetched", statementService.getRecentTransactions(accountId));
    }

    @PostMapping("/statement")
    public ApiResponse<List<TransactionResponse>> getStatement(
            @RequestHeader("X-Auth-Account-Id") Long accountId,
            @Valid @RequestBody StatementFilterRequest filter) {
        return ApiResponse.success("Account statement fetched", statementService.getStatement(accountId, filter));
    }
}
