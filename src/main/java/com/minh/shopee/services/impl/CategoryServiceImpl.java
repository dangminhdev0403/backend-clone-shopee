package com.minh.shopee.services.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.minh.shopee.domain.Category;
import com.minh.shopee.repository.CategoryRepository;
import com.minh.shopee.services.CategoryService;
import com.minh.shopee.services.utils.error.AppException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CategoryService")
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public <T> Set<T> getAllCategories(Class<T> type) {
        log.info("Fetching list of categories with projection type: {}", type.getSimpleName());
        return this.categoryRepository.findAllBy(type);

    }

    @Override
    public Category createCategory(Category category) {

        Category categoryFound = this.categoryRepository.findByName(category.getName());
        if (categoryFound != null) {
            log.error("Category already exists: {}", category);
            throw new AppException(HttpStatus.CONFLICT.value(), "Category already exists",
                    "Category " + category.getName() + " already exists");
        }

        log.info("Creating category: {}", category);
        return this.categoryRepository.save(category);
    }

    @Override
    public <T> T getCategoryById(Long id, Class<T> type) {
        log.info("Fetching category by id: {} with projection type: {}", id, type.getSimpleName());
        Optional<T> category = this.categoryRepository.findById(id, type);
        if (category.isEmpty()) {
            log.error("Category not found with id: {}", id);
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Category not found",
                    "Category with id " + id + " not found");
        }

        return category.get();
    }

    @Override
    public Category updateCategory(Category entity) {
        Category categoryFound = this.getCategoryById(entity.getId(), Category.class);
        log.info("Updating category with id {}: {}", categoryFound.getId(), categoryFound);

        categoryFound.setName(entity.getName());

        return this.categoryRepository.save(categoryFound);
    }

}
