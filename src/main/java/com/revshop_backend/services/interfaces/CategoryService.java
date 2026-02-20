package com.revshop_backend.services.interfaces;



import com.revshop_backend.model.Category;

import java.util.List;

public interface CategoryService {

    Category addCategory(Category category);

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    void deleteCategory(Long id);

    Category updateCategory(Long id, Category category);
}
