package com.apexbank.txn.repository;

import com.apexbank.txn.entity.Payee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayeeRepository extends JpaRepository<Payee, Long> {
    List<Payee> findByOwnerAccountId(Long ownerAccountId);
    Optional<Payee> findByOwnerAccountIdAndPayeeAccountNumber(Long ownerAccountId, String payeeAccountNumber);
}
