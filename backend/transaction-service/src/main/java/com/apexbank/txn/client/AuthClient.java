package com.apexbank.txn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/api/auth/internal/verify-transaction-password")
    Boolean verifyTransactionPassword(@RequestParam("userId") String userId, @RequestParam("password") String password);
}
