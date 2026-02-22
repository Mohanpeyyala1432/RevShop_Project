package com.revshop_backend;


import com.revshop_backend.exception.CategoryNotFoundException;
import com.revshop_backend.exception.ProductNotFoundException;
import com.revshop_backend.model.Category;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import com.revshop_backend.repository.CategoryRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.ReviewRepository;
import com.revshop_backend.services.implementations.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setCategoryName("Electronics");

        product = new Product();
        product.setProductName("Laptop");
        product.setCategory(category);
    }
    
    //  browseByCategory - SUCCESS

    @Test
    void browseByCategory_ShouldReturnProducts() {

        when(categoryRepository.findByCategoryNameIgnoreCase("Electronics"))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategory(category))
                .thenReturn(List.of(product));

        List<Product> result =
                productService.browseByCategory("Electronics");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getProductName());
    }

    //  browseByCategory - Category Not Found
    @Test
    void browseByCategory_ShouldThrowCategoryNotFound() {

        when(categoryRepository.findByCategoryNameIgnoreCase("Electronics"))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> productService.browseByCategory("Electronics"));
    }

    //  browseByCategory - No Products
    @Test
    void browseByCategory_ShouldThrowRuntime_WhenNoProducts() {

        when(categoryRepository.findByCategoryNameIgnoreCase("Electronics"))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategory(category))
                .thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.browseByCategory("Electronics"));

        assertEquals("No products available in this category",
                ex.getMessage());
    }

    // searchProducts - SUCCESS

    @Test
    void searchProducts_ShouldReturnProducts() {

        when(productRepository
                .findByProductNameContainingIgnoreCase("Lap"))
                .thenReturn(List.of(product));

        List<Product> result =
                productService.searchProducts("Lap");

        assertFalse(result.isEmpty());
    }

    //  searchProducts - Not Found

    @Test
    void searchProducts_ShouldThrowProductNotFound() {

        when(productRepository
                .findByProductNameContainingIgnoreCase("Mobile"))
                .thenReturn(List.of());

        assertThrows(ProductNotFoundException.class,
                () -> productService.searchProducts("Mobile"));
    }

    // getProductDetailsByName - SUCCESS
    @Test
    void getProductDetailsByName_ShouldReturnProduct() {

        when(productRepository
                .findByProductNameIgnoreCase("Laptop"))
                .thenReturn(Optional.of(product));

        Product result =
                productService.getProductDetailsByName("Laptop");

        assertEquals("Laptop", result.getProductName());
    }


    // getProductDetailsByName - Not Found
    @Test
    void getProductDetailsByName_ShouldThrowProductNotFound() {

        when(productRepository
                .findByProductNameIgnoreCase("Mobile"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductDetailsByName("Mobile"));
    }

    // getProductReviews
    @Test
    void getProductReviews_ShouldReturnReviews() {

        Review review = new Review();
        review.setComment("Good product");

        when(reviewRepository.findByProductProductId(1L))
                .thenReturn(List.of(review));

        List<Review> reviews =
                productService.getProductReviews(1L);

        assertEquals(1, reviews.size());
        assertEquals("Good product",
                reviews.get(0).getComment());
    }
}
