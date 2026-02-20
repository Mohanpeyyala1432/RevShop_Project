//package com.revshop_backend.controller;
//
//import com.revshop_backend.model.*;
//import com.revshop_backend.services.interfaces.PaymentService;
//import com.revshop_backend.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/payment")
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentController {
//
//    private final OrderRepository orderRepository;
//    private final PaymentService paymentService;
//
//    @PostMapping("/pay")
//    public ResponseEntity<String> payForOrder(@RequestBody Map<String, Object> request) {
//
//        Long orderId = Long.valueOf(request.get("orderId").toString());
//        String paymentTypeStr = request.get("paymentType").toString();
//        PaymentType paymentType = PaymentType.valueOf(paymentTypeStr);
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        Payment payment;
//
//        if (paymentType == PaymentType.DEBIT_CARD) {
//            // Card payment requires all fields
//            if (!request.containsKey("cardNumber") ||
//                    !request.containsKey("cardHolder") ||
//                    !request.containsKey("expiryDate")) {
//                return ResponseEntity.badRequest().body("Missing card details");
//            }
//
//            String cardNumber = request.get("cardNumber").toString();
//            String cardHolder = request.get("cardHolder").toString();
//            String expiryDate = request.get("expiryDate").toString();
//
//            payment = paymentService.processCardPayment(order, paymentType, cardNumber, cardHolder, expiryDate);
//
//        } else if (paymentType == PaymentType.CREDIT_CARD)
//        {
//            if (!request.containsKey("cardNumber") ||
//                    !request.containsKey("cardHolder") ||
//                    !request.containsKey("expiryDate")) {
//                return ResponseEntity.badRequest().body("Missing card details");
//            }
//
//            String cardNumber = request.get("cardNumber").toString();
//            String cardHolder = request.get("cardHolder").toString();
//            String expiryDate = request.get("expiryDate").toString();
//
//            payment = paymentService.processCardPayment(order, paymentType, cardNumber, cardHolder, expiryDate);
//        }
//        else {
//            // COD payment
//            payment = paymentService.processPayment(order, paymentType);
//        }
//
//        log.info("Payment processed for Order {} with type {}", orderId, paymentType);
//        return ResponseEntity.ok("Payment successful! Order " + order.getOrderId() + " is CONFIRMED.");
//    }
//}


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