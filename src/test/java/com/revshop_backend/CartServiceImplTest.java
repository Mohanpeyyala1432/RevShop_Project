package com.revshop_backend;

import com.revshop_backend.model.*;
import com.revshop_backend.repository.*;
import com.revshop_backend.services.implementations.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User user;
    private Product product;
    private Cart cart;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup user & security context
        user = new User();
        user.setId(1L);
        user.setEmail("mohan@gmail.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("mohan@gmail.com");
        when(userRepository.findByEmail("mohan@gmail.com")).thenReturn(Optional.of(user));

        // Product
        product = new Product();
        product.setProductId(10L);
        product.setProductName("Test Product");
        product.setPrice(100.0);

        // Cart
        cart = new Cart();
        cart.setUser(user);
        cart.setTotalAmount(0.0);

        // Stateful cart items list
        cartItems = new ArrayList<>();
        when(cartItemRepository.findByCart(cart)).thenAnswer(invocation -> cartItems);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
    }

    @Test
    void testAddToCart_NewItem() {
        // No existing cart item for this product
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        // Save adds item to the stateful list
        doAnswer(invocation -> {
            CartItem ci = invocation.getArgument(0);
            cartItems.add(ci);
            return ci;
        }).when(cartItemRepository).save(any(CartItem.class));

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        cartService.addToCart(10L, 2);

        // Verify save called
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, times(1)).save(cart);

        // Cart total should be updated
        assertEquals(200.0, cart.getTotalAmount());
    }

    @Test
    void testAddToCart_ExistingItem() {
        // Existing item with quantity 1
        CartItem existingItem = new CartItem();
        existingItem.setCart(cart);
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cartItems.add(existingItem);

        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingItem));

        doAnswer(invocation -> {
            // Just update quantity in list
            CartItem ci = invocation.getArgument(0);
            return ci;
        }).when(cartItemRepository).save(any(CartItem.class));

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        cartService.addToCart(10L, 2);

        assertEquals(3, existingItem.getQuantity());
        assertEquals(300.0, cart.getTotalAmount());
    }

    @Test
    void testGetCartItems() {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        cartItems.add(item);

        List<CartItem> items = cartService.getCartItems(user);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity());
    }

    @Test
    void testUpdateCartItemQuantity() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItems.add(cartItem);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        doAnswer(invocation -> {

            return invocation.getArgument(0);
        }).when(cartItemRepository).save(any(CartItem.class));

        cartService.updateCartItemQuantity(1L, 5);

        assertEquals(5, cartItem.getQuantity());
        assertEquals(500.0, cart.getTotalAmount());
    }

    @Test
    void testDeleteCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItems.add(cartItem);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        doAnswer(invocation -> {
            cartItems.remove(cartItem);
            return null;
        }).when(cartItemRepository).delete(cartItem);

        cartService.deleteCartItem(1L);

        assertEquals(0, cartItems.size());
        assertEquals(0.0, cart.getTotalAmount());
    }
}