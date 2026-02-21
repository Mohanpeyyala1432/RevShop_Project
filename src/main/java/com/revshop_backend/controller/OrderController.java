package com.revshop_backend.controller;

import com.revshop_backend.dto.BuyNowRequestDTO;
import com.revshop_backend.dto.CheckoutRequestDTO;
import com.revshop_backend.dto.CheckoutResponseDTO;
import com.revshop_backend.dto.OrderHistoryDTO;
import com.revshop_backend.services.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@RequestBody CheckoutRequestDTO request) {
        CheckoutResponseDTO response = orderService.checkout(request);
        return ResponseEntity.ok(response);
    }

    // Buy Now
    @PostMapping("/buy-now")
    public ResponseEntity<CheckoutResponseDTO> buyNow(@RequestBody BuyNowRequestDTO request) {
        CheckoutResponseDTO response = orderService.checkout(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory() {
        List<OrderHistoryDTO> history = orderService.getOrderHistory();
        return ResponseEntity.ok(history);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
       String msg= orderService.cancelOrder(orderId);
        return ResponseEntity.ok(msg);
    }



}
