package com.revshop_backend.repository;

import com.revshop_backend.model.Order;
import com.revshop_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByOrder(Order order);
}
