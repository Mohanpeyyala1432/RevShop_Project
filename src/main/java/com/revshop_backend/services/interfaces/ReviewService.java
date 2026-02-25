package com.revshop_backend.services.interfaces;

import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.dto.ReviewSummaryDTO;

public interface ReviewService {

    ReviewDTO addReview(Long productId, ReviewDTO reviewDTO);

    ReviewSummaryDTO getReviewsByProduct(Long productId);
}