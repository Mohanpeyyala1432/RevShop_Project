package com.revshop_backend.services;

import com.revshop_backend.model.Category;
import com.revshop_backend.repository.CategoryRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.services.implementations.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category1 = new Category();
        category1.setCategoryId(1L);
        category1.setCategoryName("Electronics");
        category1.setDescription("Electronic items");

        category2 = new Category();
        category2.setCategoryId(2L);
        category2.setCategoryName("Clothing");
        category2.setDescription("Clothing items");
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_Found() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Electronics", result.getCategoryName());
        verify(categoryRepository, times(1)).findById(1L);
    }



    @Test
    void testAddCategory() {
        when(categoryRepository.save(category1)).thenReturn(category1);

        Category result = categoryService.addCategory(category1);

        assertEquals("Electronics", result.getCategoryName());
        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    void testUpdateCategory() {
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Electronics");
        updatedCategory.setDescription("Updated description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertEquals("Updated Electronics", result.getCategoryName());
        assertEquals("Updated description", result.getDescription());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    void testDeleteCategory_ProductsExist() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsByCategory_CategoryId(1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Arrange: repository returns empty for the ID
        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());

        // Act & Assert: the service should throw RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.getCategoryById(3L)
        );

        // Verify the exception message
        assertEquals("Category not found with ID: 3", exception.getMessage());

        // Verify repository call
        verify(categoryRepository, times(1)).findById(3L);
    }
}