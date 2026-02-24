package com.revshop_backend;

import com.revshop_backend.model.*;
import com.revshop_backend.repository.OrderRepository;
import com.revshop_backend.repository.PaymentRepository;
import com.revshop_backend.services.implementations.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample order
        order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.PENDING);

        // Mock payment repository to assign ID
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(100L); // mock ID
            return payment;
        });


        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testProcessPayment_Success() {
        Payment payment = paymentService.processPayment(order, PaymentType.DEBIT_CARD);

        assertNotNull(payment);
        assertEquals(100L, payment.getPaymentId());
        assertEquals(order, payment.getOrder());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());

        // Verify order status updated
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
    }

}