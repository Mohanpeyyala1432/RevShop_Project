package com.revshop_backend.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long productId;
    private Long userId;
    private int rating;
    private String comment;
}