
package com.revshop_backend.controller;

import com.revshop_backend.dto.PaymentRequestDTO;
import com.revshop_backend.model.*;
import com.revshop_backend.repository.OrderRepository;
import com.revshop_backend.services.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<String> payForOrder(@Valid @RequestBody PaymentRequestDTO request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment;

        if (request.getType() == PaymentType.CREDIT_CARD || request.getType() == PaymentType.DEBIT_CARD) {

            payment = paymentService.processCardPayment(
                    order,
                    request.getType(),
                    request.getCardNumber(),
                    request.getCardHolderName(),
                    request.getCardExpiry()
            );

        } else {
            payment = paymentService.processPayment(order, request.getType());
        }

        log.info("Payment processed for Order {} with type {}", order.getOrderId(), request.getType());
        return ResponseEntity.ok("Payment successful! Order " + order.getOrderId() + " is CONFIRMED.");
    }
}