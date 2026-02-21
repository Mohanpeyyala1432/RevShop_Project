package com.revshop_backend.services.implementations;

import com.revshop_backend.model.Category;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.CategoryRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    private Product product;
    private User seller;
    private Category category;

    @BeforeEach
    void setUp() {

        seller = User.builder()
                .id(1L)
                .email("seller@test.com")
                .name("Seller")
                .build();

        category = new Category();
        category.setCategoryId(100L);

        product = new Product();
        product.setProductId(10L);
        product.setProductName("Laptop");
        product.setCategory(category);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "seller@test.com", null)
        );
    }

     @Test
    void testAddProduct_Success() {

        when(userRepository.findByEmail("seller@test.com"))
                .thenReturn(Optional.of(seller));

        when(categoryRepository.findById(100L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product result = productService.addProduct(product);

        assertNotNull(result);
        verify(productRepository, times(1)).save(product);
    }

     @Test
    void testUpdateProduct_NotFound() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.updateProduct(10L, product));
    }

     @Test
    void testUpdateProduct_Success() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product updated = productService.updateProduct(10L, product);

        assertNotNull(updated);
        verify(productRepository).save(product);
    }

     @Test
    void testGetLowStockProducts() {

        when(productRepository.findLowStockProducts())
                .thenReturn(List.of(product));

        List<Product> result = productService.getLowStockProducts();

        assertEquals(1, result.size());
    }

     @Test
    void testGetLowStockCount() {

        when(productRepository.countLowStockProducts())
                .thenReturn(2);

        int count = productService.getLowStockCount();

        assertEquals(2, count);
    }

     @Test
    void testGetLowStockProducts_ByThreshold() {

        when(productRepository.findByQuantityLessThan(5))
                .thenReturn(List.of(product));

        List<Product> result = productService.getLowStockProducts(5);

        assertEquals(1, result.size());
    }
}