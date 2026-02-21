package com.revshop_backend.controller;

import com.revshop_backend.model.Product;
import com.revshop_backend.services.interfaces.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        logger.info("Request to add product: {}", product);
        Product savedProduct = productService.addProduct(product);
        logger.info("Product added successfully with ID: {}", savedProduct.getProductId());
        return savedProduct;
    }

    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestBody Product product) {
        logger.info("Request to update product with ID: {} | New Data: {}", id, product);
        Product updatedProduct = productService.updateProduct(id, product);
        logger.info("Product updated successfully with ID: {}", updatedProduct.getProductId());
        return updatedProduct;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        logger.info("Request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        logger.info("Product deleted successfully with ID: {}", id);
        return "Product deleted successfully";
    }

    @GetMapping("/inventory/{sellerId}")
    public List<Product> getInventory(@PathVariable Long sellerId) {
        logger.info("Fetching inventory for seller ID: {}", sellerId);
        List<Product> inventory = productService.getSellerInventory(sellerId);
        logger.info("Found {} products for seller ID: {}", inventory.size(), sellerId);
        return inventory;
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productService.getAllProducts();
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {

        logger.info("Fetching products with low stock. Threshold: {}", threshold);
        List<Product> lowStockProducts = productService.getLowStockProducts(threshold);
        logger.info("Found {} low-stock products", lowStockProducts.size());

        return lowStockProducts;
    }

    @GetMapping("/low-stock/count")
    public ResponseEntity<Integer> getLowStockCount() {
        int count = productService.getLowStockCount();
        return ResponseEntity.ok(count);
    }
}