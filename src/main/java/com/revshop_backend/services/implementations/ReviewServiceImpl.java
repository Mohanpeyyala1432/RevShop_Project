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
import com.revshop_backend.services.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public ReviewDTO addReview(Long productId, ReviewDTO reviewDTO) {

        log.info("Attempting to add review for product ID {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        boolean hasPurchased = orderRepository
                .existsByUserAndOrderItems_Product_ProductId(user, productId);

        if (!hasPurchased) {
            log.warn("User {} tried to review product {} without purchase",
                    user.getEmail(), productId);
            throw new IllegalStateException(
                    "You can only review products you have purchased"
            );
        }

        reviewRepository.findByUserAndProduct(user, product)
                .ifPresent(existing -> {
                    log.warn("User {} already reviewed product {}",
                            user.getEmail(), productId);
                    throw new IllegalStateException(
                            "You have already reviewed this product"
                    );
                });

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setProduct(product);
        review.setUser(user);

        Review savedReview = reviewRepository.save(review);

        log.info("Review successfully added for product ID {} by user {}",
                productId, user.getEmail());

        return mapToDTO(savedReview);
    }

    @Override
    public ReviewSummaryDTO getReviewsByProduct(Long productId) {

        log.info("Fetching reviews for product ID {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });

        List<ReviewDTO> reviewList = reviewRepository.findByProduct(product)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        Double avg = reviewRepository.findAverageRatingByProductId(productId);

        log.info("Total reviews found for product {}: {}",
                productId, reviewList.size());

        return new ReviewSummaryDTO(
                avg != null ? avg : 0.0,
                reviewList.size(),
                reviewList
        );
    }

    private ReviewDTO mapToDTO(Review review) {

        ReviewDTO dto = new ReviewDTO();
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserName(review.getUser().getName());
        dto.setCreatedAt(review.getCreatedAt());

        return dto;
    }
}