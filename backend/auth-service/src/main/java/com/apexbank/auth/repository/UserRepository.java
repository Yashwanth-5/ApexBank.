package com.apexbank.auth.repository;

import com.apexbank.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{

    Optional<User> findByUserId(String userId);

    Optional<User> findByAccountNumber(String accountNumber);

    boolean existsByUserId(String userId);
}