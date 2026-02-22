package com.revshop_backend.controller;

import com.revshop_backend.dto.SellerOrderViewDTO;
import com.revshop_backend.model.OrderStatus;
import com.revshop_backend.services.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/orders")
@RequiredArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<List<SellerOrderViewDTO>> getSellerOrders() {
        // Automatically fetch orders for logged-in seller
        List<SellerOrderViewDTO> orders = orderService.getOrdersForSeller();
        return ResponseEntity.ok(orders);
    }


    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatusBySeller(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        orderService.updateOrderStatusBySeller(orderId, status);
        return ResponseEntity.ok("Order " + orderId + " status updated to " + status);
    }
}
