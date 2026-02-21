package com.revshop_backend.services.implementations;

import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.dto.ReviewSummaryDTO;
import com.revshop_backend.exception.ResourceNotFoundException;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.OrderRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.ReviewRepository;
import com.revshop_backend.repository.UserRepository;

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
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Vijay")
                .email("vijay@test.com")
                .password("password")
                .build();

        product = new Product();
        product.setProductId(10L);

        // Mock Security Context (simulate logged-in user)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "vijay@test.com", null)
        );
    }

     @Test
    void testAddReview_Success() {

        ReviewDTO dto = new ReviewDTO();
        dto.setRating(5);
        dto.setComment("Excellent product");

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail("vijay@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.hasUserPurchasedProduct(user, 10L))
                .thenReturn(true);

        when(reviewRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.empty());

        Review savedReview = new Review();
        savedReview.setRating(5);
        savedReview.setComment("Excellent product");
        savedReview.setUser(user);
        savedReview.setProduct(product);

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(savedReview);

        ReviewDTO result = reviewService.addReview(10L, dto);

        assertEquals(5, result.getRating());
        assertEquals("Excellent product", result.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

     @Test
    void testAddReview_ProductNotFound() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.addReview(10L, new ReviewDTO()));
    }

     @Test
    void testAddReview_UserNotFound() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail("vijay@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.addReview(10L, new ReviewDTO()));
    }

     @Test
    void testAddReview_UserNotPurchased() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail("vijay@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.hasUserPurchasedProduct(user, 10L))
                .thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> reviewService.addReview(10L, new ReviewDTO()));
    }

     @Test
    void testAddReview_DuplicateReview() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail("vijay@test.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.hasUserPurchasedProduct(user, 10L))
                .thenReturn(true);

        when(reviewRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(new Review()));

        assertThrows(IllegalStateException.class,
                () -> reviewService.addReview(10L, new ReviewDTO()));
    }

     @Test
    void testGetReviewsByProduct_Success() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        Review review = new Review();
        review.setRating(4);
        review.setComment("Good product");
        review.setUser(user);

        when(reviewRepository.findByProduct(product))
                .thenReturn(List.of(review));

        when(reviewRepository.findAverageRatingByProductId(10L))
                .thenReturn(4.0);

        ReviewSummaryDTO summary =
                reviewService.getReviewsByProduct(10L);

        assertEquals(1, summary.getTotalReviews());
        assertEquals(4.0, summary.getAverageRating());
    }

     @Test
    void testGetReviews_ProductNotFound() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewsByProduct(10L));
    }
}