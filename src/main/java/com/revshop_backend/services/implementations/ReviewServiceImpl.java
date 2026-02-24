package com.revshop_backend.services.implementations;

import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.dto.ReviewResponseDTO;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.ReviewRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.services.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addReview(ReviewDTO reviewDTO) {

        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();

        reviewRepository.save(review);
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Review> reviews = reviewRepository.findByProduct(product);

        return reviews.stream()
                .map(r -> new ReviewResponseDTO(
                        r.getUser().getName(),
                        r.getRating(),
                        r.getComment()))
                .collect(Collectors.toList());
    }
}