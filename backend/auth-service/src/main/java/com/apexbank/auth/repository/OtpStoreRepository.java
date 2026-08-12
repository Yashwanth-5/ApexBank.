package com.apexbank.auth.repository;

import com.apexbank.auth.entity.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpStoreRepository extends JpaRepository<OtpStore,Long>{

    Optional<OtpStore> findTopByIdentifierAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            String identifier,
            String purpose
    );
}