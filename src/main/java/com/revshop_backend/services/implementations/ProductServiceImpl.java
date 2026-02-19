package com.revshop_backend.services.implementations;

import com.revshop_backend.model.Product;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.services.interfaces.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public Product addProduct(Product product) {
        product.setIsActive(true);
        logger.info("Adding new product: {}", product.getProductName());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product updatedProduct) {
        logger.info("Updating product with ID: {}", productId);

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", productId);
                    return new RuntimeException("Product not found");
                });

        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setMrp(updatedProduct.getMrp());
        existingProduct.setDiscount(updatedProduct.getDiscount());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setLowStockThreshold(updatedProduct.getLowStockThreshold());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        logger.info("Deleting product with ID: {}", productId);
        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> getSellerInventory(Long sellerId) {
        logger.info("Fetching inventory for seller ID: {}", sellerId);
        return productRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        logger.warn("Fetching products with stock less than: {}", threshold);
        return productRepository.findByQuantityLessThan(threshold);
    }
}
