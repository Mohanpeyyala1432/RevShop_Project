package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;

import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    Product updateProduct(Long productId, Product product);

    void deleteProduct(Long productId);

    List<Product> getSellerInventory(Long sellerId);

    List<Product> getLowStockProducts();

    List<Product> getAllProducts();

    int getLowStockCount();


    List<Product> getLowStockProducts(Integer threshold);




    // Buyer Side

    List<Product> browseByCategory(String categoryName);

    List<Product> searchProducts(String keyword);

    Product getProductDetailsByName(String productName);




}







