package com.revshop_backend.repository;

import com.revshop_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellerId(Long sellerId);


    @Query("SELECT p FROM Product p WHERE p.quantity <= p.lowStockThreshold")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.lowStockThreshold")
    int countLowStockProducts();

    boolean existsByCategory_CategoryId(Long categoryId);


    List<Product> findByQuantityLessThan(Integer threshold);
}
