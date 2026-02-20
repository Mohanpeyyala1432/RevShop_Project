package com.revshop_backend.services.implementations;

import com.revshop_backend.exception.CategoryNotFoundException;
import com.revshop_backend.exception.ProductNotFoundException;
import com.revshop_backend.model.Category;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import com.revshop_backend.repository.CategoryRepository;
import com.revshop_backend.model.Product;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.ReviewRepository;
import com.revshop_backend.services.interfaces.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository; // Inject CategoryRepository

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Autowired
    private ReviewRepository reviewRepository;


    @Override
    public Product addProduct(Product product) {
        product.setIsActive(true);
        logger.info("Adding new product: {}", product.getProductName());

        // Validate category
        if (product.getCategory() == null || product.getCategory().getCategoryId() == null) {
            throw new RuntimeException("Category is required to add a product");
        }

        Long categoryId = product.getCategory().getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        product.setCategory(category); // Attach valid category
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

        // Validate category if updated
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getCategoryId() != null) {
            Long categoryId = updatedProduct.getCategory().getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
            existingProduct.setCategory(category);
        }

        // Update other product fields
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setMrp(updatedProduct.getMrp());
        existingProduct.setDiscount(updatedProduct.getDiscount());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setLowStockThreshold(updatedProduct.getLowStockThreshold());
        existingProduct.setIsActive(updatedProduct.getIsActive());

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
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public int getLowStockCount() {
        return productRepository.findLowStockProducts().size(); // count
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        logger.warn("Fetching products with stock less than: {}", threshold);
        return productRepository.findByQuantityLessThan(threshold);
    }





    @Override
    public List<Product> browseByCategory(String categoryName) {

        // 1️ Check if category exists
        Category category = categoryRepository
                .findByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category is not available")
                );

        // 2️ Fetch products
        List<Product> products = productRepository.findByCategory(category);

        // 3️ If no products in that category
        if (products.isEmpty()) {
            throw new RuntimeException("No products available in this category");
        }

        return products;
    }



    @Override
    public List<Product> searchProducts(String keyword) {

        List<Product> products =
                productRepository.findByProductNameContainingIgnoreCase(keyword);

        if (products.isEmpty()) {
            throw new ProductNotFoundException(
                    "Products not available based on your search: " + keyword
            );
        }

        return products;
    }


    @Override
    public Product getProductDetailsByName(String productName) {

        logger.info("Fetching product details for name: {}", productName);

        return productRepository
                .findByProductNameIgnoreCase(productName)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product '" + productName + "' is not available"
                        )
                );
    }

    @Override
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductProductId(productId);
    }
}


