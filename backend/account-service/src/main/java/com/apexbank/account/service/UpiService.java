package com.apexbank.account.service;

import com.apexbank.account.dto.request.SetUpiIdRequest;
import com.apexbank.account.dto.response.UpiIdResponse;
import com.apexbank.account.entity.Account;
import com.apexbank.account.entity.UpiId;
import com.apexbank.account.exception.DuplicateResourceException;
import com.apexbank.account.exception.ResourceNotFoundException;
import com.apexbank.account.repository.AccountRepository;
import com.apexbank.account.repository.UpiIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpiService {

    private final UpiIdRepository upiIdRepository;
    private final AccountRepository accountRepository;

    public UpiIdResponse getMyUpiId(Long accountId) {
        UpiId upiId = upiIdRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No UPI ID found for this account. Please create one."));
        return UpiIdResponse.builder().upiId(upiId.getUpiId()).active(upiId.getActive()).build();
    }

    @Transactional
    public UpiIdResponse setUpiId(Long accountId, SetUpiIdRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        String prefix = (request.getCustomPrefix() == null || request.getCustomPrefix().isBlank())
                ? account.getMobileNumber() : request.getCustomPrefix();
        String newUpiId = prefix + "@apex";

        if (upiIdRepository.existsByUpiId(newUpiId)) {
            throw new DuplicateResourceException("This UPI ID is already taken. Please choose another.");
        }

        UpiId existing = upiIdRepository.findByAccountId(accountId).orElse(null);
        if (existing != null) {
            existing.setUpiId(newUpiId);
            upiIdRepository.save(existing);
            return UpiIdResponse.builder().upiId(existing.getUpiId()).active(existing.getActive()).build();
        }

        UpiId upiId = UpiId.builder().upiId(newUpiId).accountId(accountId).active(true).build();
        upiIdRepository.save(upiId);
        return UpiIdResponse.builder().upiId(upiId.getUpiId()).active(true).build();
    }
}
