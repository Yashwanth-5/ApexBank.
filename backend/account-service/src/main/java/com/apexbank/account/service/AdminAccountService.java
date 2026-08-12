package com.apexbank.account.service;

import com.apexbank.account.dto.request.AdminCreditRequest;
import com.apexbank.account.dto.request.UpdateProfileRequest;
import com.apexbank.account.dto.response.AdminAccountResponse;
import com.apexbank.account.dto.response.AdminSystemStats;
import com.apexbank.account.entity.Account;
import com.apexbank.account.exception.ResourceNotFoundException;
import com.apexbank.account.repository.AccountOpeningRequestRepository;
import com.apexbank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final AccountRepository accountRepository;
    private final AccountOpeningRequestRepository requestRepository;

    public List<AdminAccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
    }

    public AdminAccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return mapToAdminResponse(account);
    }

    @Transactional
    public void updateAccount(String accountNumber, UpdateProfileRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setMobileNumber(request.getMobileNumber());
        account.setEmail(request.getEmail());
        account.setResidentialAddressLine1(request.getResidentialAddressLine1());
        account.setResidentialAddressLine2(request.getResidentialAddressLine2());
        account.setResidentialLandmark(request.getResidentialLandmark());
        account.setResidentialState(request.getResidentialState());
        account.setResidentialCity(request.getResidentialCity());
        account.setResidentialPincode(request.getResidentialPincode());
        account.setPermanentAddressLine1(request.getPermanentAddressLine1());
        account.setPermanentAddressLine2(request.getPermanentAddressLine2());
        account.setPermanentLandmark(request.getPermanentLandmark());
        account.setPermanentState(request.getPermanentState());
        account.setPermanentCity(request.getPermanentCity());
        account.setPermanentPincode(request.getPermanentPincode());
        account.setOccupationType(request.getOccupationType());

        accountRepository.save(account);
    }

    @Transactional
    public AdminAccountResponse creditAccount(AdminCreditRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        Account updated = accountRepository.save(account);
        
        return mapToAdminResponse(updated);
    }

    public AdminSystemStats getSystemStats() {
        long totalUsers = accountRepository.count();
        BigDecimal totalBalance = accountRepository.findAll().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingRequests = requestRepository.findByStatus("PENDING").size();
        long approvedRequests = requestRepository.findByStatus("APPROVED").size();
        long rejectedRequests = requestRepository.findByStatus("REJECTED").size();

        return AdminSystemStats.builder()
                .totalUsers(totalUsers)
                .totalBalance(totalBalance)
                .pendingRequests(pendingRequests)
                .approvedRequests(approvedRequests)
                .rejectedRequests(rejectedRequests)
                .totalTransactions(0L) // Will be populated by transaction-service if needed
                .totalTransactionVolume(BigDecimal.ZERO)
                .build();
    }

    private AdminAccountResponse mapToAdminResponse(Account account) {
        return AdminAccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .title(account.getTitle())
                .firstName(account.getFirstName())
                .middleName(account.getMiddleName())
                .lastName(account.getLastName())
                .mobileNumber(account.getMobileNumber())
                .email(account.getEmail())
                .aadharNumber(account.getAadharNumber())
                .dateOfBirth(account.getDateOfBirth())
                .residentialAddressLine1(account.getResidentialAddressLine1())
                .residentialAddressLine2(account.getResidentialAddressLine2())
                .residentialLandmark(account.getResidentialLandmark())
                .residentialState(account.getResidentialState())
                .residentialCity(account.getResidentialCity())
                .residentialPincode(account.getResidentialPincode())
                .permanentAddressLine1(account.getPermanentAddressLine1())
                .permanentAddressLine2(account.getPermanentAddressLine2())
                .permanentLandmark(account.getPermanentLandmark())
                .permanentState(account.getPermanentState())
                .permanentCity(account.getPermanentCity())
                .permanentPincode(account.getPermanentPincode())
                .occupationType(account.getOccupationType())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .hasDebitCard(account.getHasDebitCard())
                .netBankingEnabled(account.getNetBankingEnabled())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}