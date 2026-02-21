package com.revshop_backend.services.implementations;

import com.revshop_backend.dto.*;
import com.revshop_backend.exception.ResourceNotFoundException;
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

        User user = cartService.getLoggedInUser();
        log.info("Checkout started for user: {}", user.getEmail());

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

         Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID {}", savedOrder.getOrderId());

         List<OrderItem> orderItems = cartItems.stream().map(ci -> {

            Product product = ci.getProduct();

            int updatedStock = product.getQuantity() - ci.getQuantity();
            product.setQuantity(updatedStock);
            productRepository.save(product);

            log.info("Stock updated for product {} | Remaining: {}",
                    product.getProductName(), updatedStock);

             if (product.getLowStockThreshold() != null &&
                    updatedStock < product.getLowStockThreshold()) {

                notificationService.sendNotification(
                        product.getSeller(),
                        "Low stock alert for product: " + product.getProductName()
                );

                log.warn("Low stock alert sent for product {}",
                        product.getProductName());
            }

            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProduct(product);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(product.getPrice());

            return oi;

        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

         notificationService.sendNotification(user,
                "Your order #" + savedOrder.getOrderId() +
                        " has been placed successfully! Total: ₹" + savedOrder.getTotalAmount());

        log.info("Buyer notified for order {}", savedOrder.getOrderId());

         for (OrderItem item : orderItems) {
            notificationService.sendNotification(
                    item.getProduct().getSeller(),
                    "New order received for product: " +
                            item.getProduct().getProductName()
            );

            log.info("Seller notified for product {}",
                    item.getProduct().getProductName());
        }

         cartItemRepository.deleteAll(cartItems);
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        log.info("Cart cleared for user {}", user.getEmail());

        return new CheckoutResponseDTO(
                savedOrder.getOrderId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount()
        );
    }

    @Override
    public CheckoutResponseDTO checkout(BuyNowRequestDTO request) {

        User user = cartService.getLoggedInUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        double totalAmount = product.getPrice() * request.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        order = orderRepository.save(order);
        log.info("BuyNow order created with ID {}", order.getOrderId());

         int updatedStock = product.getQuantity() - request.getQuantity();
        product.setQuantity(updatedStock);
        productRepository.save(product);

        log.info("Stock updated for product {} | Remaining: {}",
                product.getProductName(), updatedStock);

         if (product.getLowStockThreshold() != null &&
                updatedStock < product.getLowStockThreshold()) {

            notificationService.sendNotification(
                    product.getSeller(),
                    "Low stock alert for product: " + product.getProductName()
            );

            log.warn("Low stock alert sent for product {}",
                    product.getProductName());
        }

         OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(product.getPrice());

        orderItemRepository.save(orderItem);

         notificationService.sendNotification(user,
                "Your order #" + order.getOrderId() +
                        " has been placed successfully! Total: ₹" + order.getTotalAmount());

         notificationService.sendNotification(
                product.getSeller(),
                "New order received for product: " +
                        product.getProductName()
        );

        log.info("BuyNow notifications completed for order {}", order.getOrderId());

        return new CheckoutResponseDTO(
                order.getOrderId(),
                order.getStatus().name(),
                order.getTotalAmount()
        );
    }

    @Override
    public List<OrderHistoryDTO> getOrderHistory() {

        User user = cartService.getLoggedInUser();

        List<Order> orders = orderRepository.findByUserOrderByOrderIdDesc(user);

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