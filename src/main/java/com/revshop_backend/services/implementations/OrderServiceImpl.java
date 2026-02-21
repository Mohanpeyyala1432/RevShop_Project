
package com.revshop_backend.services.implementations;

import com.revshop_backend.dto.*;
import com.revshop_backend.exception.CartNotFoundException;
import com.revshop_backend.exception.EmptyCartException;
import com.revshop_backend.exception.NoOrdersFoundException;
import com.revshop_backend.exception.ProductNotFoundException;
import com.revshop_backend.model.*;
import com.revshop_backend.repository.*;
import com.revshop_backend.security.AuthUtil;
import com.revshop_backend.services.interfaces.NotificationService;
import com.revshop_backend.services.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request) {
        log.info("Checkout started for user: {}", cartService.getLoggedInUser().getEmail());

        // 1. Get logged-in user
        User user = cartService.getLoggedInUser();
        log.info("Logged-in user fetched: {}", user.getEmail());

        // 2. Get user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot checkout because cart is empty");
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

        // 4. Convert CartItems -> OrderItems
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

        // 5a. Decrease product quantities
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }

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
    @Transactional
    public CheckoutResponseDTO checkout(BuyNowRequestDTO request) {
        User user = cartService.getLoggedInUser();

        // Fetch the product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Check stock
        if (request.getQuantity() > product.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }

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

        // Reduce product quantity
        product.setQuantity(product.getQuantity() - request.getQuantity());
        productRepository.save(product);

        notificationService.sendNotification(user,
                "Your order #" + order.getOrderId() +
                        " has been placed successfully! Total: ₹" + order.getTotalAmount());
        log.info("Notification sent for BuyNow Order ID: {} to user: {}", order.getOrderId(), user.getEmail());

        return new CheckoutResponseDTO(order.getOrderId(), order.getStatus().name(), order.getTotalAmount());
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

    @Override
    public List<SellerOrderViewDTO> getOrdersForSeller() {
        User seller = authUtil.getLoggedInUser();
        Long sellerId = seller.getId();

        List<Order> allOrders = orderRepository.findAll();

        List<SellerOrderViewDTO> sellerOrders = new ArrayList<>();

        for (Order order : allOrders) {
            User buyer = order.getUser();

            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();

                if (product.getSellerId().equals(sellerId)) {
                    SellerOrderViewDTO dto = new SellerOrderViewDTO(
                            order.getOrderId(),
                            buyer.getName(),
                            buyer.getEmail(),
                            buyer.getPhone(),
                            product.getProductName(),
                            item.getQuantity(),
                            item.getPrice(),
                            item.getQuantity() * item.getPrice()
                    );
                    sellerOrders.add(dto);
                }
            }
        }

        if (sellerOrders.isEmpty()) {
            throw new NoOrdersFoundException("No orders found for your products");
        }

        return sellerOrders;
    }

    @Override
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrdersFoundException("Order not found with ID: " + orderId));

        // Cannot cancel if shipped or delivered
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel an order that is already " + order.getStatus());
        }

        // Restore product quantities
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        //  Update order status
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        //  Build response message with payment info
        StringBuilder message = new StringBuilder("Order " + order.getOrderId() + " has been cancelled successfully.");

        paymentRepository.findByOrder(order).ifPresent(payment -> {
            if ((payment.getType() == PaymentType.CREDIT_CARD || payment.getType() == PaymentType.DEBIT_CARD)
                    && payment.getStatus() == PaymentStatus.SUCCESS) {
                message.append(" Payment will be refunded to your card.");
            }
        });

        return message.toString();
    }


    @Override
    public void updateOrderStatusBySeller(Long orderId, OrderStatus status) {
        //  Get logged-in seller
        User seller = authUtil.getLoggedInUser();
        Long sellerId = seller.getId();

        // Fetch order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrdersFoundException("Order not found with ID: " + orderId));

        // Check if this seller has any products in this order
        boolean sellerHasProducts = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getSellerId().equals(sellerId));

        if (!sellerHasProducts) {
            throw new RuntimeException("You cannot update this order. No products belong to you.");
        }


        order.setStatus(status);
        orderRepository.save(order);
    }
}