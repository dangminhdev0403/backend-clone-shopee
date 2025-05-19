package com.minh.shopee.services;

import java.io.IOException;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.model.Category;

public interface CategoryService {
    <T> Set<T> getAllCategories(Class<T> type);

    Category createCategory(Category category);

    void createListCategory(MultipartFile fileName) throws IOException;

    <T> T getCategoryById(Long id, Class<T> type);

    Category updateCategory(Category entity) ;

    void deleteCategory(Long id);
}
