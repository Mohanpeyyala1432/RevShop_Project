package com.revshop_backend.repository;

import com.revshop_backend.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordOtpRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findTopByEmailOrderByIdDesc(String email);

    void deleteByEmail(String email);
}

