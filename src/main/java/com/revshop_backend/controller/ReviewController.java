package com.revshop_backend.controller;

import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.dto.ReviewSummaryDTO;
import com.revshop_backend.services.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{productId}")
    public ReviewDTO addReview(@PathVariable Long productId,
                               @RequestBody ReviewDTO reviewDTO) {

        return reviewService.addReview(productId, reviewDTO);
    }

    @GetMapping("/{productId}")
    public ReviewSummaryDTO getReviews(@PathVariable Long productId) {

        return reviewService.getReviewsByProduct(productId);
    }
}