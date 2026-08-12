package com.apexbank.account.repository;

import com.apexbank.account.entity.UpiId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpiIdRepository extends JpaRepository<UpiId, Long> {
    Optional<UpiId> findByUpiId(String upiId);
    Optional<UpiId> findByAccountId(Long accountId);
    boolean existsByUpiId(String upiId);
}
