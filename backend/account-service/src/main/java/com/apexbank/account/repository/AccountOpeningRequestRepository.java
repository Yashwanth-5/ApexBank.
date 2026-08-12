package com.apexbank.account.repository;

import com.apexbank.account.entity.AccountOpeningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOpeningRequestRepository extends JpaRepository<AccountOpeningRequest, Long> {
    List<AccountOpeningRequest> findByStatus(String status);
}
