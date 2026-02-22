package com.revshop_backend.services.interfaces;

import com.revshop_backend.dto.*;
import com.revshop_backend.model.OrderStatus;

import java.util.List;

public interface OrderService {

    CheckoutResponseDTO checkout(CheckoutRequestDTO request);

    CheckoutResponseDTO checkout(BuyNowRequestDTO request);

    List<OrderHistoryDTO> getOrderHistory();

   List<SellerOrderViewDTO> getOrdersForSeller();

    String cancelOrder(Long orderId);

    void updateOrderStatusBySeller(Long orderId, OrderStatus status);
}
