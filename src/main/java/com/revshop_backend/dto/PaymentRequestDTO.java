package com.revshop_backend.dto;

import com.revshop_backend.model.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment type is required")
    private PaymentType type;


    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    @Pattern(regexp = "\\d*", message = "Card number must contain only digits")
    private String cardNumber;


    private String cardHolderName;


    @Pattern(regexp = "(0[1-9]|1[0-2])\\/\\d{2}", message = "Expiry must be in MM/YY format")
    private String cardExpiry;
}