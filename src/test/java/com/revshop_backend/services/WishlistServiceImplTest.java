package com.revshop_backend.services;

import com.revshop_backend.exception.ResourceNotFoundException;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import com.revshop_backend.model.Wishlist;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.repository.WishlistRepository;
import com.revshop_backend.services.implementations.WishlistServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Raj")
                .email("Raj@test.com")
                .password("pass")
                .build();

        product = new Product();
        product.setProductId(10L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "Raj@test.com", null)
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAddToWishlist_Success() {

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(wishlistRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.empty());

        wishlistService.addToWishlist(10L);

        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void testAddToWishlist_ProductNotFound() {

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.addToWishlist(10L));
    }

    @Test
    void testAddToWishlist_DuplicateProduct() {

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(wishlistRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(new Wishlist()));

        assertThrows(IllegalStateException.class,
                () -> wishlistService.addToWishlist(10L));
    }

    @Test
    void testAddToWishlist_UserNotFound() {

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.addToWishlist(10L));
    }

    @Test
    void testRemoveFromWishlist_Success() {

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(wishlistRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(wishlist));

        wishlistService.removeFromWishlist(10L);

        verify(wishlistRepository).delete(wishlist);
    }

    @Test
    void testRemoveFromWishlist_NotFound() {

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(wishlistRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.removeFromWishlist(10L));
    }

    @Test
    void testGetWishlist_Success() {

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        when(userRepository.findByEmail("Raj@test.com"))
                .thenReturn(Optional.of(user));

        when(wishlistRepository.findByUser(user))
                .thenReturn(List.of(wishlist));

        List<Wishlist> result = wishlistService.getWishlist();

        assertEquals(1, result.size());
    }
}