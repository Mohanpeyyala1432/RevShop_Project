package com.revshop_backend.services.implementations;

import com.revshop_backend.model.*;
import com.revshop_backend.repository.OrderRepository;
import com.revshop_backend.repository.PaymentRepository;
import com.revshop_backend.services.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // COD
    @Override
    public Payment processPayment(Order order, PaymentType type) {
        log.info("Processing payment for Order {} with type {}", order.getOrderId(), type);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setType(type);
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return savedPayment;
    }


    @Override
    public Payment processCardPayment(Order order, PaymentType type, String cardNumber, String cardHolder, String expiryDate) {

        log.info("Processing CARD payment for Order {} with card holder {}", order.getOrderId(), cardHolder);


        if (cardNumber == null || cardNumber.length() != 16) {
            throw new IllegalArgumentException("Invalid card number");
        }

        if (cardHolder == null || cardHolder.isBlank()) {
            throw new IllegalArgumentException("Card holder name required");
        }

        if (expiryDate == null || expiryDate.isBlank()) {
            throw new IllegalArgumentException("Expiry date required");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setType(type);
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return savedPayment;
    }
}