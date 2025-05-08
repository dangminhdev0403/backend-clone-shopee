package com.minh.shopee.services;

import java.util.Optional;
import java.util.Set;

import com.minh.shopee.domain.Category;

public interface CategoryService {
    <T> Set<T> getAllCategories(Class<T> type);

    Category createCategory(Category category);

    <T> T getCategoryById(Long id, Class<T> type);

    Category updateCategory(Category entity);
}
