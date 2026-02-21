package com.revshop_backend.services.implementations;

import com.revshop_backend.model.Category;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.CategoryRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.services.interfaces.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Product addProduct(Product product) {

        logger.info("Adding new product: {}", product.getProductName());

        // Get logged-in seller from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        product.setSeller(seller);
        product.setIsActive(true);

        // Validate category
        Long categoryId = product.getCategory().getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product updatedProduct) {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

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
        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> getSellerInventory(Long sellerId) {
        return productRepository.findBySeller_Id(sellerId);
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
        return productRepository.countLowStockProducts();
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }
}