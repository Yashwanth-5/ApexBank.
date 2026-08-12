package com.apexbank.account.service;

import com.apexbank.account.dto.request.AccountOpeningRequestDto;
import com.apexbank.account.dto.response.AccountApprovedResponse;
import com.apexbank.account.dto.response.AccountOpeningRequestResponse;
import com.apexbank.account.entity.Account;
import com.apexbank.account.entity.AccountOpeningRequest;
import com.apexbank.account.entity.UpiId;
import com.apexbank.account.exception.DuplicateResourceException;
import com.apexbank.account.exception.ResourceNotFoundException;
import com.apexbank.account.repository.AccountOpeningRequestRepository;
import com.apexbank.account.repository.AccountRepository;
import com.apexbank.account.repository.UpiIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountOpeningRequestRepository requestRepository;
    private final AccountRepository accountRepository;
    private final UpiIdRepository upiIdRepository;

    @Transactional
    public AccountOpeningRequestResponse submitAccountOpeningRequest(AccountOpeningRequestDto dto) {
        if (accountRepository.existsByAadharNumber(dto.getAadharNumber())) {
            throw new DuplicateResourceException("An account already exists with this Aadhar number");
        }

        String permAddr1 = dto.isPermanentSameAsResidential() ? dto.getResidentialAddressLine1() : dto.getPermanentAddressLine1();
        String permAddr2 = dto.isPermanentSameAsResidential() ? dto.getResidentialAddressLine2() : dto.getPermanentAddressLine2();
        String permLandmark = dto.isPermanentSameAsResidential() ? dto.getResidentialLandmark() : dto.getPermanentLandmark();
        String permState = dto.isPermanentSameAsResidential() ? dto.getResidentialState() : dto.getPermanentState();
        String permCity = dto.isPermanentSameAsResidential() ? dto.getResidentialCity() : dto.getPermanentCity();
        String permPincode = dto.isPermanentSameAsResidential() ? dto.getResidentialPincode() : dto.getPermanentPincode();

        AccountOpeningRequest request = AccountOpeningRequest.builder()
                .title(dto.getTitle())
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .fatherName(dto.getFatherName())
                .mobileNumber(dto.getMobileNumber())
                .email(dto.getEmail())
                .aadharNumber(dto.getAadharNumber())
                .dateOfBirth(dto.getDateOfBirth())
                .residentialAddressLine1(dto.getResidentialAddressLine1())
                .residentialAddressLine2(dto.getResidentialAddressLine2())
                .residentialLandmark(dto.getResidentialLandmark())
                .residentialState(dto.getResidentialState())
                .residentialCity(dto.getResidentialCity())
                .residentialPincode(dto.getResidentialPincode())
                .permanentAddressLine1(permAddr1)
                .permanentAddressLine2(permAddr2)
                .permanentLandmark(permLandmark)
                .permanentState(permState)
                .permanentCity(permCity)
                .permanentPincode(permPincode)
                .occupationType(dto.getOccupationType())
                .sourceOfIncome(dto.getSourceOfIncome())
                .grossAnnualIncome(dto.getGrossAnnualIncome())
                .wantsDebitCard(dto.getWantsDebitCard())
                .optForNetBanking(dto.isOptForNetBanking())
                .status("PENDING")
                .build();

        AccountOpeningRequest saved = requestRepository.save(request);
        return mapToResponse(saved);
    }

    public List<AccountOpeningRequestResponse> getPendingRequests() {
        return requestRepository.findByStatus("PENDING").stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AccountOpeningRequestResponse> getAllRequests() {
        return requestRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AccountOpeningRequestResponse getRequestById(Long id) {
        return mapToResponse(requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account opening request not found")));
    }

    @Transactional
    public AccountApprovedResponse approveRequest(Long requestId) {
        AccountOpeningRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Account opening request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        String accountNumber;
        do {
            accountNumber = AccountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .requestId(request.getId())
                .title(request.getTitle())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .aadharNumber(request.getAadharNumber())
                .dateOfBirth(request.getDateOfBirth())
                .residentialAddressLine1(request.getResidentialAddressLine1())
                .residentialAddressLine2(request.getResidentialAddressLine2())
                .residentialLandmark(request.getResidentialLandmark())
                .residentialState(request.getResidentialState())
                .residentialCity(request.getResidentialCity())
                .residentialPincode(request.getResidentialPincode())
                .permanentAddressLine1(request.getPermanentAddressLine1())
                .permanentAddressLine2(request.getPermanentAddressLine2())
                .permanentLandmark(request.getPermanentLandmark())
                .permanentState(request.getPermanentState())
                .permanentCity(request.getPermanentCity())
                .permanentPincode(request.getPermanentPincode())
                .occupationType(request.getOccupationType())
                .accountType("SAVINGS")
                .balance(BigDecimal.ZERO)
                .hasDebitCard(request.getWantsDebitCard())
                .netBankingEnabled(request.getOptForNetBanking())
                .status("ACTIVE")
                .build();

        Account savedAccount = accountRepository.save(account);

        String upiIdValue = savedAccount.getMobileNumber() + "@apex";
        if (!upiIdRepository.existsByUpiId(upiIdValue)) {
            upiIdRepository.save(UpiId.builder()
                    .upiId(upiIdValue)
                    .accountId(savedAccount.getId())
                    .active(true)
                    .build());
        }

        request.setStatus("APPROVED");
        requestRepository.save(request);

        return AccountApprovedResponse.builder()
                .accountId(savedAccount.getId())
                .accountNumber(savedAccount.getAccountNumber())
                .fullName(savedAccount.getFullName())
                .status(savedAccount.getStatus())
                .build();
    }

    @Transactional
    public void rejectRequest(Long requestId, String reason) {
        AccountOpeningRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Account opening request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        request.setStatus("REJECTED");
        request.setRejectionReason(reason);
        requestRepository.save(request);
    }

    private AccountOpeningRequestResponse mapToResponse(AccountOpeningRequest r) {
        return AccountOpeningRequestResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .firstName(r.getFirstName())
                .middleName(r.getMiddleName())
                .lastName(r.getLastName())
                .fatherName(r.getFatherName())
                .mobileNumber(r.getMobileNumber())
                .email(r.getEmail())
                .aadharNumber(r.getAadharNumber())
                .dateOfBirth(r.getDateOfBirth())
                .residentialAddressLine1(r.getResidentialAddressLine1())
                .residentialCity(r.getResidentialCity())
                .residentialState(r.getResidentialState())
                .residentialPincode(r.getResidentialPincode())
                .permanentAddressLine1(r.getPermanentAddressLine1())
                .permanentCity(r.getPermanentCity())
                .permanentState(r.getPermanentState())
                .permanentPincode(r.getPermanentPincode())
                .occupationType(r.getOccupationType())
                .sourceOfIncome(r.getSourceOfIncome())
                .grossAnnualIncome(r.getGrossAnnualIncome())
                .wantsDebitCard(r.getWantsDebitCard())
                .optForNetBanking(r.getOptForNetBanking())
                .status(r.getStatus())
                .rejectionReason(r.getRejectionReason())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
