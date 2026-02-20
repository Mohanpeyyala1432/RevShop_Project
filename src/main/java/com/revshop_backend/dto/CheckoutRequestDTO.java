package com.revshop_backend.dto;

import lombok.Data;

@Data
public class CheckoutRequestDTO {

    private String shippingAddress;
    private String billingAddress;
    private String contactNumber;
}
