package com.revshop_backend.controller;

import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.dto.ReviewResponseDTO;
import com.revshop_backend.services.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Add Review
    @PostMapping
    public String addReview(@RequestBody ReviewDTO reviewDTO) {
        reviewService.addReview(reviewDTO);
        return "Review added successfully";
    }

    // Get Reviews for Product
    @GetMapping("/product/{productId}")
    public List<ReviewResponseDTO> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }
}