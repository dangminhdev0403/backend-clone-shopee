package com.minh.shopee.controllers.admin;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.CategoryDTO;
import com.minh.shopee.domain.model.Category;
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
    public ResponseEntity<Set<CategoryDTO>> getAllCategories() {
        Set<CategoryDTO> categories = this.categoryService.getAllCategories(CategoryDTO.class);

        return ResponseEntity.ok(categories);
    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@RequestParam("file") MultipartFile  file,
            @RequestBody Category category) {
                
        Category categoryCreated = this.categoryService.createCategory(category);
        log.info("Category created: {}", category);
        return ResponseEntity.ok(categoryCreated);
    }


    @PutMapping()
    public ResponseEntity<Category> updateCategory(@RequestBody Category entity) {
        Category categoryUpdated = this.categoryService.updateCategory(entity);
        log.info("Category updated: {}", categoryUpdated);
        return ResponseEntity.ok(categoryUpdated);

    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteCategory(Long id) {
        this.categoryService.deleteCategory(id);
        log.info("Category deleted with id: {}", id);
        return ResponseEntity.ok().build();
    }

}
