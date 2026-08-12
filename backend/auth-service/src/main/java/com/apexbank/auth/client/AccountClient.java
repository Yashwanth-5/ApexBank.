package com.apexbank.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client used by auth-service to look up account details on
 * account-service during Register-for-Internet-Banking and Forgot User ID.
 * "account-service" here is the Eureka application name, resolved via
 * client-side load balancing (no hardcoded host/port).
 */
@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/api/internal/accounts/by-number/{accountNumber}")
    AccountInternalDto getAccountByNumber(@PathVariable("accountNumber") String accountNumber);

    record AccountInternalDto(
            Long id,
            String accountNumber,
            String firstName,
            String lastName,
            String fullName,
            String status
    ) {}
}
