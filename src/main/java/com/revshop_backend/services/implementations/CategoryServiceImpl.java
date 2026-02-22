package com.revshop_backend.services.implementations;



import com.revshop_backend.exception.CategoryNotFoundException;
import com.revshop_backend.model.Category;
import com.revshop_backend.repository.CategoryRepository;

import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.services.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Category addCategory(Category category) {


        Optional<Category> existingCategory =
                categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName());

        if (existingCategory.isPresent()) {
            throw new CategoryNotFoundException("Category already exists: " + category.getCategoryName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }



    @Override
    public void deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            if (productRepository.existsByCategory_CategoryId(id)) {
                throw new RuntimeException("Cannot delete category. Products exist under this category.");
            }
            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Category not found with ID: " + id);
        }
    }


    @Override
    public Category updateCategory(Long id, Category category) {
        Optional<Category> existingCategoryOpt = categoryRepository.findById(id);
        if (existingCategoryOpt.isPresent()) {
            Category existingCategory = existingCategoryOpt.get();
            existingCategory.setCategoryName(category.getCategoryName());
            existingCategory.setDescription(category.getDescription());
            return categoryRepository.save(existingCategory);
        } else {
            throw new RuntimeException("Category not found with ID: " + id);
        }
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }
}
