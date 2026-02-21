package com.revshop_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // Proper relationship with seller (User)
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    private String productName;
    private String description;
    private Double price;
    private Double mrp;
    private Double discount;
    private Integer quantity;
    private Integer lowStockThreshold;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}