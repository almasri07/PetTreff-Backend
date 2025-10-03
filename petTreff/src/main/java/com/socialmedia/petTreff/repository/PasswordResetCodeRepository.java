package com.socialmedia.petTreff.repository;

import com.socialmedia.petTreff.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findTopByEmailAndUsedFalseOrderByIdDesc(String email);
}