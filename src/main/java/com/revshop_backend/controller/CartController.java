package com.revshop_backend.controller;

import com.revshop_backend.dto.CartItemDTO;
import com.revshop_backend.dto.CartResponseDTO;
import com.revshop_backend.model.CartItem;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.services.implementations.CartServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buyer/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private UserRepository userRepository;

    // Add product
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody Map<String, Object> request) {
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        log.info("API Hit → Add To Cart | ProductId: {} | Quantity: {}", productId, quantity);
        cartService.addToCart(productId, quantity);

        return ResponseEntity.ok(getUpdatedCartResponse());
    }

    // View cart
    @GetMapping("/view")
    public ResponseEntity<CartResponseDTO> viewCart() {
        log.info("API Hit → View Cart");
        return ResponseEntity.ok(getUpdatedCartResponse());
    }

    // Update cart
    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateCartItem(@RequestBody Map<String, Object> request) {
        Long cartItemId = Long.valueOf(request.get("cartItemId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        log.info("API Hit → Update Cart Item | CartItemId: {} | Quantity: {}", cartItemId, quantity);
        cartService.updateCartItemQuantity(cartItemId, quantity);

        return ResponseEntity.ok(getUpdatedCartResponse());
    }

    // Delete cart item
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartResponseDTO> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(getUpdatedCartResponse());
    }


    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }


    private CartResponseDTO getUpdatedCartResponse() {
        User user = cartService.getLoggedInUser();
        List<CartItem> cartItems = cartService.getCartItems(user);

        List<CartItemDTO> cartDTOs = cartItems.stream().map(item ->
                new CartItemDTO(
                        item.getId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getProductName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                )
        ).toList();

        double totalAmount = cartDTOs.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        log.info("Returning {} items with totalAmount {} for user: {}", cartDTOs.size(), totalAmount, user.getEmail());

        return new CartResponseDTO(cartDTOs, totalAmount);
    }


}



