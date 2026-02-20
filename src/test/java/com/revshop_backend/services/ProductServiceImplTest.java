package com.revshop_backend.services;

import com.revshop_backend.model.Category;
import com.revshop_backend.model.Product;
import com.revshop_backend.repository.CategoryRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.services.implementations.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Laptop");
        product.setCategory(category);
        product.setQuantity(10);
        product.setLowStockThreshold(5);
        product.setIsActive(true);
    }

    @Test
    void testAddProductSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.addProduct(product);
        assertEquals("Laptop", saved.getProductName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testAddProductCategoryMissing() {
        Product newProduct = new Product();
        Exception exception = assertThrows(RuntimeException.class, () -> productService.addProduct(newProduct));
        assertTrue(exception.getMessage().contains("Category is required"));
    }

    @Test
    void testUpdateProductSuccess() {
        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Laptop");
        updatedProduct.setCategory(category);
        updatedProduct.setQuantity(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);
        assertEquals("Updated Laptop", result.getProductName());
        assertEquals(20, result.getQuantity());
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);
        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetSellerInventory() {
        when(productRepository.findBySellerId(100L)).thenReturn(Arrays.asList(product));
        assertEquals(1, productService.getSellerInventory(100L).size());
    }

    @Test
    void testGetLowStockProducts() {
        when(productRepository.findLowStockProducts()).thenReturn(Arrays.asList(product));
        assertEquals(1, productService.getLowStockProducts().size());
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    void testGetLowStockCount() {
        when(productRepository.findLowStockProducts()).thenReturn(Arrays.asList(product));
        assertEquals(1, productService.getLowStockCount());
    }
}