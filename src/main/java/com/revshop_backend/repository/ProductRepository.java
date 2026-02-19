package com.revshop_backend.repository;

import com.revshop_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellerId(Long sellerId);

    List<Product> findByQuantityLessThan(Integer threshold);
}
