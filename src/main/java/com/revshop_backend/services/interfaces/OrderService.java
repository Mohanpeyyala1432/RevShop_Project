package com.revshop_backend.services.interfaces;

import com.revshop_backend.dto.BuyNowRequestDTO;
import com.revshop_backend.dto.CheckoutRequestDTO;
import com.revshop_backend.dto.CheckoutResponseDTO;
import com.revshop_backend.dto.OrderHistoryDTO;

import java.util.List;

public interface OrderService {

    CheckoutResponseDTO checkout(CheckoutRequestDTO request);

    CheckoutResponseDTO checkout(BuyNowRequestDTO request);

    List<OrderHistoryDTO> getOrderHistory();

}
