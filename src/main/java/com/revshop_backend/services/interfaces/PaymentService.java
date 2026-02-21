package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.Order;
import com.revshop_backend.model.Payment;
import com.revshop_backend.model.PaymentType;

public interface PaymentService {

    Payment processPayment(Order order, PaymentType type);

    Payment processCardPayment(Order order, PaymentType type,
                               String cardNumber, String cardHolder, String expiryDate);
}
