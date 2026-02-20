package com.revshop_backend.controller;



import com.revshop_backend.model.*;
import com.revshop_backend.services.interfaces.PaymentService;
import com.revshop_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<String> payForOrder(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String paymentTypeStr = request.get("paymentType").toString();
        PaymentType paymentType = PaymentType.valueOf(paymentTypeStr);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        paymentService.processPayment(order, paymentType);

        log.info("Payment processed for Order {} with type {}", orderId, paymentType);
        return ResponseEntity.ok("Payment successful! Order " + order.getOrderId() + " is CONFIRMED.");
    }
}

