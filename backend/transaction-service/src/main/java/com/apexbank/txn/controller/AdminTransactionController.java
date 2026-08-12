package com.apexbank.txn.controller;

import com.apexbank.txn.dto.response.ApiResponse;
import com.apexbank.txn.dto.response.TransactionResponse;
import com.apexbank.txn.service.AdminTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final AdminTransactionService adminTransactionService;

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions(
            @RequestHeader("X-Auth-Roles") String roles) {
        
        log.info("GET /api/admin/transactions - Admin roles: {}", roles);
        
        try {
            // Validate admin access
            requireAdmin(roles);
            
            // Fetch transactions
            List<TransactionResponse> transactions = adminTransactionService.getAllTransactions();
            
            log.info("Successfully fetched {} transactions", transactions.size());
            
            return ResponseEntity.ok(
                ApiResponse.success("All transactions fetched successfully", transactions)
            );
            
        } catch (SecurityException e) {
            log.warn("Access denied for /api/admin/transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Admin access required"));
                
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions/health")
    public ResponseEntity<ApiResponse<String>> checkDatabaseHealth(
            @RequestHeader("X-Auth-Roles") String roles) {
        
        log.info("GET /api/admin/transactions/health - Admin roles: {}", roles);
        
        try {
            requireAdmin(roles);
            
            long count = adminTransactionService.getTransactionCount();
            
            String healthStatus = String.format("Database connection healthy. Found %d transactions.", count);
            log.info("Database health check successful: {}", healthStatus);
            
            return ResponseEntity.ok(ApiResponse.success(healthStatus, healthStatus));
            
        } catch (SecurityException e) {
            log.warn("Access denied for /api/admin/transactions/health: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Admin access required"));
                
        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database connection failed: " + e.getMessage()));
        }
    }

    private void requireAdmin(String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            throw new SecurityException("Admin access required");
        }
    }
}