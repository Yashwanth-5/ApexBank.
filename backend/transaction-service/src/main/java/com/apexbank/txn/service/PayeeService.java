package com.apexbank.txn.service;

import com.apexbank.txn.client.AccountClient;
import com.apexbank.txn.dto.request.AddPayeeRequest;
import com.apexbank.txn.dto.response.PayeeResponse;
import com.apexbank.txn.entity.Payee;
import com.apexbank.txn.exception.DuplicateResourceException;
import com.apexbank.txn.exception.ResourceNotFoundException;
import com.apexbank.txn.repository.PayeeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayeeService {

    private final PayeeRepository payeeRepository;
    private final AccountClient accountClient;

    @Transactional
    public PayeeResponse addPayee(Long ownerAccountId, String ownerAccountNumber, AddPayeeRequest request) {
        if (!request.getPayeeAccountNumber().equals(request.getConfirmPayeeAccountNumber())) {
            throw new IllegalArgumentException("Account number and confirm account number do not match");
        }

        if (request.getPayeeAccountNumber().equals(ownerAccountNumber)) {
            throw new IllegalArgumentException("You cannot add your own account as a payee");
        }

        try {
            accountClient.getByAccountNumber(request.getPayeeAccountNumber());
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Beneficiary account number not found");
        }

        if (payeeRepository.findByOwnerAccountIdAndPayeeAccountNumber(ownerAccountId, request.getPayeeAccountNumber()).isPresent()) {
            throw new DuplicateResourceException("This beneficiary is already saved");
        }

        Payee payee = Payee.builder()
                .ownerAccountId(ownerAccountId)
                .payeeName(request.getPayeeName())
                .payeeAccountNumber(request.getPayeeAccountNumber())
                .nickname(request.getNickname())
                .build();

        Payee saved = payeeRepository.save(payee);

        return PayeeResponse.builder()
                .id(saved.getId())
                .payeeName(saved.getPayeeName())
                .payeeAccountNumber(saved.getPayeeAccountNumber())
                .nickname(saved.getNickname())
                .build();
    }

    public List<PayeeResponse> getPayees(Long ownerAccountId) {
        return payeeRepository.findByOwnerAccountId(ownerAccountId).stream()
                .map(p -> PayeeResponse.builder()
                        .id(p.getId())
                        .payeeName(p.getPayeeName())
                        .payeeAccountNumber(p.getPayeeAccountNumber())
                        .nickname(p.getNickname())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePayee(Long ownerAccountId, Long payeeId) {
        Payee payee = payeeRepository.findById(payeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Payee not found"));

        if (!payee.getOwnerAccountId().equals(ownerAccountId)) {
            throw new IllegalStateException("You are not authorized to delete this payee");
        }

        payeeRepository.delete(payee);
    }
}
