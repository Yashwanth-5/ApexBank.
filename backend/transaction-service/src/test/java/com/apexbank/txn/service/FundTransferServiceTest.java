package com.apexbank.txn.service;

import com.apexbank.txn.client.AccountClient;
import com.apexbank.txn.client.AccountClient.AccountInternalDto;
import com.apexbank.txn.client.AccountClient.DebitCreditRequest;
import com.apexbank.txn.client.AuthClient;
import com.apexbank.txn.dto.request.NeftTransferRequest;
import com.apexbank.txn.dto.request.UpiTransferRequest;
import com.apexbank.txn.dto.response.TransferSuccessResponse;
import com.apexbank.txn.entity.Transaction;
import com.apexbank.txn.exception.InvalidCredentialsException;
import com.apexbank.txn.exception.ResourceNotFoundException;
import com.apexbank.txn.repository.TransactionRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundTransferServiceTest {

    @Mock private AccountClient accountClient;
    @Mock private AuthClient authClient;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private FundTransferService fundTransferService;

    private AccountInternalDto fromAccount;
    private AccountInternalDto toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new AccountInternalDto(1L, "100000000001", "Arjun", "Rao", "Arjun Rao", "ACTIVE", new BigDecimal("50000.00"));
        toAccount = new AccountInternalDto(2L, "100000000002", "Rahul", "Verma", "Rahul Verma", "ACTIVE", new BigDecimal("75000.00"));
    }

    @Test
    void transferNeft_withValidRequest_succeeds() {
        NeftTransferRequest request = new NeftTransferRequest();
        request.setToAccountNumber("100000000002");
        request.setAmount(new BigDecimal("2500.00"));
        request.setRemarks("Rent");
        request.setTransactionPassword("Txn1234!");

        when(authClient.verifyTransactionPassword("arjun.rao", "Txn1234!")).thenReturn(true);
        when(accountClient.getById(1L)).thenReturn(fromAccount);
        when(accountClient.getByAccountNumber("100000000002")).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TransferSuccessResponse response = fundTransferService.transferNeft(1L, "arjun.rao", request);

        assertNotNull(response);
        assertEquals("NEFT", response.getMode());
        assertEquals(new BigDecimal("2500.00"), response.getAmount());

        verify(accountClient).debit(eq(1L), any(DebitCreditRequest.class));
        verify(accountClient).credit(eq(2L), any(DebitCreditRequest.class));
    }

    @Test
    void transferNeft_withWrongTransactionPassword_throwsInvalidCredentials() {
        NeftTransferRequest request = new NeftTransferRequest();
        request.setToAccountNumber("100000000002");
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionPassword("WrongPwd");

        when(authClient.verifyTransactionPassword("arjun.rao", "WrongPwd")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> fundTransferService.transferNeft(1L, "arjun.rao", request));

        verify(accountClient, never()).debit(any(), any());
    }

    @Test
    void transferNeft_toSelf_throwsIllegalArgumentException() {
        NeftTransferRequest request = new NeftTransferRequest();
        request.setToAccountNumber("100000000001");
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionPassword("Txn1234!");

        when(authClient.verifyTransactionPassword("arjun.rao", "Txn1234!")).thenReturn(true);
        when(accountClient.getById(1L)).thenReturn(fromAccount);
        when(accountClient.getByAccountNumber("100000000001")).thenReturn(fromAccount);

        assertThrows(IllegalArgumentException.class,
                () -> fundTransferService.transferNeft(1L, "arjun.rao", request));
    }

    @Test
    void transferNeft_toNonExistentAccount_throwsResourceNotFound() {
        NeftTransferRequest request = new NeftTransferRequest();
        request.setToAccountNumber("999999999999");
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionPassword("Txn1234!");

        Request feignRequest = Request.create(Request.HttpMethod.GET, "url",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, new RequestTemplate());

        when(authClient.verifyTransactionPassword("arjun.rao", "Txn1234!")).thenReturn(true);
        when(accountClient.getById(1L)).thenReturn(fromAccount);
        when(accountClient.getByAccountNumber("999999999999"))
                .thenThrow(new FeignException.NotFound("not found", feignRequest, null, null));

        assertThrows(ResourceNotFoundException.class,
                () -> fundTransferService.transferNeft(1L, "arjun.rao", request));
    }

    @Test
    void transferUpi_viaUpiId_succeedsAndRecordsUpiIdUsed() {
        UpiTransferRequest request = new UpiTransferRequest();
        request.setToUpiId("9988776655@apex");
        request.setAmount(new BigDecimal("500.00"));
        request.setRemarks("Lunch");
        request.setTransactionPassword("Txn1234!");

        when(authClient.verifyTransactionPassword("arjun.rao", "Txn1234!")).thenReturn(true);
        when(accountClient.getById(1L)).thenReturn(fromAccount);
        when(accountClient.resolveUpiId("9988776655@apex")).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        TransferSuccessResponse response = fundTransferService.transferUpi(1L, "arjun.rao", request);

        assertEquals("UPI", response.getMode());
        assertEquals("9988776655@apex", response.getToUpiId());
    }

    @Test
    void debitThenCredit_whenCreditFails_compensatesByRecreditingSource() {
        NeftTransferRequest request = new NeftTransferRequest();
        request.setToAccountNumber("100000000002");
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionPassword("Txn1234!");

        when(authClient.verifyTransactionPassword("arjun.rao", "Txn1234!")).thenReturn(true);
        when(accountClient.getById(1L)).thenReturn(fromAccount);
        when(accountClient.getByAccountNumber("100000000002")).thenReturn(toAccount);

        // debit succeeds, but credit throws — service should compensate by crediting fromAccount back
        when(accountClient.credit(eq(2L), any(DebitCreditRequest.class)))
                .thenThrow(new RuntimeException("account-service unavailable"));

        assertThrows(IllegalStateException.class,
                () -> fundTransferService.transferNeft(1L, "arjun.rao", request));

        verify(accountClient).debit(eq(1L), any(DebitCreditRequest.class));
        verify(accountClient).credit(eq(2L), any(DebitCreditRequest.class));
        verify(accountClient).credit(eq(1L), any(DebitCreditRequest.class)); // compensation
        verify(transactionRepository, never()).save(any());
    }
}
