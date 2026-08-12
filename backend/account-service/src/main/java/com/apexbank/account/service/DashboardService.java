package com.apexbank.account.service;

import com.apexbank.account.dto.request.StatementFilterRequest;
import com.apexbank.account.dto.request.UpdateProfileRequest;
import com.apexbank.account.dto.response.AccountSummaryResponse;
import com.apexbank.account.dto.response.DashboardResponse;
import com.apexbank.account.dto.response.UserProfileResponse;
import com.apexbank.account.entity.Account;
import com.apexbank.account.entity.UpiId;
import com.apexbank.account.exception.ResourceNotFoundException;
import com.apexbank.account.repository.AccountRepository;
import com.apexbank.account.repository.UpiIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final UpiIdRepository upiIdRepository;

    public DashboardResponse getDashboard(Long accountId) {
        Account account = getAccount(accountId);
        String upiId = upiIdRepository.findByAccountId(accountId).map(UpiId::getUpiId).orElse(null);

        return DashboardResponse.builder()
                .accountNumber(account.getAccountNumber())
                .fullName(account.getFullName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .upiId(upiId)
                .build();
    }

    public AccountSummaryResponse getAccountSummary(Long accountId) {
        Account account = getAccount(accountId);
        return AccountSummaryResponse.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }

    public UserProfileResponse getProfile(Long accountId) {
        Account account = getAccount(accountId);
        String upiId = upiIdRepository.findByAccountId(accountId).map(UpiId::getUpiId).orElse(null);

        return UserProfileResponse.builder()
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
                .upiId(upiId)
                .hasDebitCard(account.getHasDebitCard())
                .netBankingEnabled(account.getNetBankingEnabled())
                .build();
    }

    @Transactional
    public void updateProfile(Long accountId, UpdateProfileRequest request) {
        Account account = getAccount(accountId);
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

    // Statement filtering now lives in transaction-service since it owns Transaction records;
    // kept here as a stub note for clarity. Angular calls transaction-service's
    // /api/fund-transfer/statement endpoint directly for date-range statements.

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }
}
