package com.apexbank.txn.controller;

import com.apexbank.txn.dto.request.AddPayeeRequest;
import com.apexbank.txn.dto.response.ApiResponse;
import com.apexbank.txn.dto.response.PayeeResponse;
import com.apexbank.txn.service.PayeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payees")
@RequiredArgsConstructor
public class PayeeController {

    private final PayeeService payeeService;

    @PostMapping
    public ApiResponse<PayeeResponse> addPayee(
            @RequestHeader("X-Auth-Account-Id") Long accountId,
            @RequestHeader(value = "X-Auth-Account-Number", required = false) String accountNumber,
            @Valid @RequestBody AddPayeeRequest request) {
        return ApiResponse.success("Beneficiary added successfully", payeeService.addPayee(accountId, accountNumber, request));
    }

    @GetMapping
    public ApiResponse<List<PayeeResponse>> getPayees(@RequestHeader("X-Auth-Account-Id") Long accountId) {
        return ApiResponse.success("Beneficiaries fetched", payeeService.getPayees(accountId));
    }

    @DeleteMapping("/{payeeId}")
    public ApiResponse<Void> deletePayee(
            @RequestHeader("X-Auth-Account-Id") Long accountId, @PathVariable Long payeeId) {
        payeeService.deletePayee(accountId, payeeId);
        return ApiResponse.success("Beneficiary removed");
    }
}
