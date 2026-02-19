package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.Product;
import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    Product updateProduct(Long productId, Product product);

    void deleteProduct(Long productId);

    List<Product> getSellerInventory(Long sellerId);

    List<Product> getLowStockProducts();

    List<Product> getAllProducts();

    int getLowStockCount();


}
