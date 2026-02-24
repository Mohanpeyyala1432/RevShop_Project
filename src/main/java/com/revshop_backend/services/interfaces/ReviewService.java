package com.revshop_backend.services.interfaces;

import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.dto.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {

    void addReview(ReviewDTO reviewDTO);

    List<ReviewResponseDTO> getReviewsByProduct(Long productId);
}