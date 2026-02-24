package com.revshop_backend.repository;

import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);
}