package com.revshop_backend.controller;


import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import com.revshop_backend.services.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/products")
public class BuyerController {

    @Autowired
    private ProductService productService;



    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> browseByCategory(@PathVariable String categoryName) {

        List<Product> products = productService.browseByCategory(categoryName);

        return ResponseEntity.ok(products);
    }

    // Search by keyword
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {

        return productService.searchProducts(keyword);
    }

     // View product details by name
     @GetMapping("/name/{productName}")
     public Product getProductDetailsByName(@PathVariable String productName) {
     return productService.getProductDetailsByName(productName);
    }

}
