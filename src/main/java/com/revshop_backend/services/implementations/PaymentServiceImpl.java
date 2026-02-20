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

    @Override
    public Payment processPayment(Order order, PaymentType type) {
        log.info("Processing payment for Order {} with type {}", order.getOrderId(), type);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setType(type);
        payment.setStatus(PaymentStatus.SUCCESS); // Simulated as successful

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment {} saved successfully", savedPayment.getPaymentId());

        // Update order status
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        log.info("Order {} status updated to CONFIRMED", order.getOrderId());

        return savedPayment;
    }
}
