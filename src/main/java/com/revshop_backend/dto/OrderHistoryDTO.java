package com.revshop_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderHistoryDTO {

    private Long orderId;
    private String status;          // OrderStatus
    private Double totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private List<OrderItemDTO> items; // Reuse CartItemDTO or make new OrderItemDTO
}
