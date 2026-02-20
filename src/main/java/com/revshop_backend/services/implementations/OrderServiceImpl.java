package com.revshop_backend.services.implementations;

import com.revshop_backend.dto.*;
import com.revshop_backend.model.*;
import com.revshop_backend.repository.*;
import com.revshop_backend.services.interfaces.NotificationService;
import com.revshop_backend.services.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartServiceImpl cartService;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;


    @Override
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request) {
        log.info("Checkout started for user: {}", cartService.getLoggedInUser().getEmail());

        // 1. Get logged-in user
        User user = cartService.getLoggedInUser();
        log.info("Logged-in user fetched: {}", user.getEmail());

        // 2. Get user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Cart not found for user: {}", user.getEmail());
                    return new RuntimeException("Cart not found");
                });

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            log.warn("Cart is empty for user: {}", user.getEmail());
            throw new RuntimeException("Cart is empty");
        }

        log.info("Found {} items in cart for user: {}", cartItems.size(), user.getEmail());

        // 3. Create and save Order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} for user: {}", savedOrder.getOrderId(), user.getEmail());

        // 4. Convert CartItems → OrderItems
        List<OrderItem> orderItems = cartItems.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            log.info("OrderItem prepared | Product: {} | Quantity: {} | Price: {}",
                    ci.getProduct().getProductName(), ci.getQuantity(), ci.getProduct().getPrice());
            return oi;
        }).collect(Collectors.toList());

        // 5. Save all order items
        orderItemRepository.saveAll(orderItems);
        log.info("All order items saved for Order ID: {}", savedOrder.getOrderId());

        //6. Send notification
        notificationService.sendNotification(user,
                "Your order #" + savedOrder.getOrderId() +
                        " has been placed successfully! Total: ₹" + savedOrder.getTotalAmount());
        log.info("Notification sent for Order ID: {} to user: {}", savedOrder.getOrderId(), user.getEmail());

        // 7. Clear user's cart
        cartItemRepository.deleteAll(cartItems);
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
        log.info("Cart cleared for user: {}", user.getEmail());

        // 8. Return response
        CheckoutResponseDTO response = new CheckoutResponseDTO(
                savedOrder.getOrderId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount()
        );

        log.info("Checkout completed successfully for Order ID: {} | Total Amount: {}",
                savedOrder.getOrderId(), savedOrder.getTotalAmount());

        return response;
    }

    @Override
    public CheckoutResponseDTO checkout(BuyNowRequestDTO request) {
        User user = cartService.getLoggedInUser();

        // Fetch the product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Calculate total amount
        double totalAmount = product.getPrice() * request.getQuantity();

        // Create Order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);

        log.info("BuyNow Order created | OrderId: {} | User: {} | Total: {}", order.getOrderId(), user.getEmail(), totalAmount);

        // Create OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItemRepository.save(orderItem);

        log.info("OrderItem created | OrderItemId: {} | Product: {} | Quantity: {}", orderItem.getId(), product.getProductName(), orderItem.getQuantity());

        notificationService.sendNotification(user,
                "Your order #" + order.getOrderId() +
                        " has been placed successfully! Total: ₹" + order.getTotalAmount());
        log.info("Notification sent for BuyNow Order ID: {} to user: {}", order.getOrderId(), user.getEmail());

        return new CheckoutResponseDTO(order.getOrderId(), order.getStatus().name(), order.getTotalAmount());
    }


    @Override
    public List<OrderHistoryDTO> getOrderHistory() {
        User user = cartService.getLoggedInUser();

        List<Order> orders = orderRepository.findByUserOrderByOrderIdDesc(user); // Fetch orders for user

        return orders.stream().map(order -> {
            List<OrderItemDTO> items = order.getOrderItems().stream().map(oi ->
                    new OrderItemDTO(
                            oi.getProduct().getProductId(),
                            oi.getProduct().getProductName(),
                            oi.getPrice(),
                            oi.getQuantity()
                    )
            ).toList();

            return new OrderHistoryDTO(
                    order.getOrderId(),
                    order.getStatus().name(),
                    order.getTotalAmount(),
                    order.getShippingAddress(),
                    order.getBillingAddress(),
                    items
            );
        }).toList();
    }


}
