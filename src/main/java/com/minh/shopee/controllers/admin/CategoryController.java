package com.minh.shopee.controllers.admin;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minh.shopee.domain.Category;
import com.minh.shopee.services.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j(topic = "CategoryController")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<Set<Category>> getAllCategories() {
        Set<Category> categories = this.categoryService.getAllCategories(Category.class);

        return ResponseEntity.ok(categories);
    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {

        Category categoryCreated = this.categoryService.createCategory(category);
        log.info("Category created: {}", category);
        return ResponseEntity.ok(categoryCreated);
    }

    @PutMapping()
    public ResponseEntity<Category> updateCategory(@RequestBody Category entity) {
        Category categoryUpdated = this.categoryService.updateCategory(entity);
        return ResponseEntity.ok(categoryUpdated);
    }

}
