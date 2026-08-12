package com.apexbank.account.service;

import com.apexbank.account.dto.request.AccountOpeningRequestDto;
import com.apexbank.account.dto.response.AccountApprovedResponse;
import com.apexbank.account.dto.response.AccountOpeningRequestResponse;
import com.apexbank.account.entity.Account;
import com.apexbank.account.entity.AccountOpeningRequest;
import com.apexbank.account.exception.DuplicateResourceException;
import com.apexbank.account.exception.ResourceNotFoundException;
import com.apexbank.account.repository.AccountOpeningRequestRepository;
import com.apexbank.account.repository.AccountRepository;
import com.apexbank.account.repository.UpiIdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountOpeningRequestRepository requestRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private UpiIdRepository upiIdRepository;

    @InjectMocks
    private AccountService accountService;

    private AccountOpeningRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = new AccountOpeningRequestDto();
        dto.setTitle("Mr");
        dto.setFirstName("Rahul");
        dto.setLastName("Verma");
        dto.setFatherName("Suresh Verma");
        dto.setMobileNumber("9988776655");
        dto.setAadharNumber("345678901234");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 30));
        dto.setResidentialAddressLine1("Plot 45");
        dto.setResidentialState("Telangana");
        dto.setResidentialCity("Hyderabad");
        dto.setResidentialPincode("500033");
        dto.setPermanentSameAsResidential(true);
        dto.setPermanentAddressLine1("Plot 45");
        dto.setPermanentState("Telangana");
        dto.setPermanentCity("Hyderabad");
        dto.setPermanentPincode("500033");
        dto.setOccupationType("Salaried");
        dto.setSourceOfIncome("Salary");
        dto.setGrossAnnualIncome("10-15 LPA");
        dto.setWantsDebitCard(true);
        dto.setOptForNetBanking(true);
        dto.setAgreeTerms(true);
    }

    @Test
    void submitAccountOpeningRequest_withNewAadhar_savesRequestAsPending() {
        when(accountRepository.existsByAadharNumber("345678901234")).thenReturn(false);
        when(requestRepository.save(any(AccountOpeningRequest.class))).thenAnswer(inv -> {
            AccountOpeningRequest r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        AccountOpeningRequestResponse response = accountService.submitAccountOpeningRequest(dto);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void submitAccountOpeningRequest_withDuplicateAadhar_throwsException() {
        when(accountRepository.existsByAadharNumber("345678901234")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> accountService.submitAccountOpeningRequest(dto));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void approveRequest_forPendingRequest_createsActiveAccountAndUpiId() {
        AccountOpeningRequest request = AccountOpeningRequest.builder()
                .id(1L).title("Mr").firstName("Rahul").lastName("Verma")
                .mobileNumber("9988776655").aadharNumber("345678901234")
                .dateOfBirth(LocalDate.of(1990, 1, 30))
                .residentialAddressLine1("Plot 45").residentialState("Telangana")
                .residentialCity("Hyderabad").residentialPincode("500033")
                .permanentAddressLine1("Plot 45").permanentState("Telangana")
                .permanentCity("Hyderabad").permanentPincode("500033")
                .occupationType("Salaried").wantsDebitCard(true).optForNetBanking(true)
                .status("PENDING").build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });
        when(upiIdRepository.existsByUpiId("9988776655@apex")).thenReturn(false);

        AccountApprovedResponse response = accountService.approveRequest(1L);

        assertEquals("ACTIVE", response.getStatus());
        verify(upiIdRepository).save(any());
        verify(requestRepository).save(argThat(r -> "APPROVED".equals(r.getStatus())));
    }

    @Test
    void approveRequest_whenAlreadyProcessed_throwsIllegalStateException() {
        AccountOpeningRequest request = AccountOpeningRequest.builder().id(1L).status("APPROVED").build();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class, () -> accountService.approveRequest(1L));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void approveRequest_withNonExistentId_throwsResourceNotFoundException() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accountService.approveRequest(99L));
    }
}
