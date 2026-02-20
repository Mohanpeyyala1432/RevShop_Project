package com.revshop_backend;

import com.revshop_backend.dto.*;
import com.revshop_backend.model.*;
import com.revshop_backend.repository.*;
import com.revshop_backend.services.implementations.CartServiceImpl;
import com.revshop_backend.services.implementations.OrderServiceImpl;
import com.revshop_backend.services.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CartServiceImpl cartService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User user;
    private Cart cart;
    private Product product;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock logged-in user
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        when(cartService.getLoggedInUser()).thenReturn(user);

        // SecurityContext mocks
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());

        // Cart & CartItems
        cart = new Cart();
        cart.setUser(user);
        cart.setTotalAmount(200.0); // example total
        cartItems = new ArrayList<>();
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(cartItems);

        // Product
        product = new Product();
        product.setProductId(10L);
        product.setProductName("Test Product");
        product.setPrice(100.0);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        // Mock order repository save to assign realistic IDs
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getOrderId() == null) {
                order.setOrderId(new Random().nextLong() & Long.MAX_VALUE); // positive ID
            }
            return order;
        });

        // Mock order item save/saveAll
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock cart repository save to accept updates
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testCheckoutCart() {
        // Add cart item
        CartItem ci = new CartItem();
        ci.setCart(cart);
        ci.setProduct(product);
        ci.setQuantity(2);
        cartItems.add(ci);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setShippingAddress("123 Street");
        request.setBillingAddress("123 Street");

        CheckoutResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PENDING.name(), response.getStatus());
        assertEquals(200.0, response.getTotalAmount()); // Total of cart items
        assertEquals(0.0, cart.getTotalAmount()); // Cart should be cleared

        // Verify repository interactions
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
        verify(cartItemRepository, times(1)).deleteAll(cartItems);
        verify(cartRepository, times(1)).save(cart);
        verify(notificationService, times(1)).sendNotification(eq(user), contains("has been placed successfully"));
    }

    @Test
    void testCheckoutBuyNow() {
        BuyNowRequestDTO request = new BuyNowRequestDTO();
        request.setProductId(10L);
        request.setQuantity(2);
        request.setShippingAddress("456 Street");
        request.setBillingAddress("456 Street");

        CheckoutResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PENDING.name(), response.getStatus());
        assertEquals(200.0, response.getTotalAmount()); // 2 * 100

        // Verify repository interactions
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(notificationService, times(1)).sendNotification(eq(user), contains("has been placed successfully"));
    }

    @Test
    void testGetOrderHistory() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(200.0);

        OrderItem oi = new OrderItem();
        oi.setOrder(order);
        oi.setProduct(product);
        oi.setQuantity(2);
        oi.setPrice(product.getPrice());

        order.setOrderItems(List.of(oi));

        when(orderRepository.findByUserOrderByOrderIdDesc(user)).thenReturn(List.of(order));

        List<OrderHistoryDTO> history = orderService.getOrderHistory();

        assertEquals(1, history.size());
        assertEquals(1L, history.get(0).getOrderId());
        assertEquals(200.0, history.get(0).getTotalAmount());
        assertEquals(1, history.get(0).getItems().size());
    }
}