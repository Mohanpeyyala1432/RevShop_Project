package com.revshop_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {

    private Integer rating;

    private String comment;

    private String userName;

    private LocalDateTime createdAt;
}