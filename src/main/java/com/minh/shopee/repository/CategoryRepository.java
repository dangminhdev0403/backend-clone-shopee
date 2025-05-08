package com.minh.shopee.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.minh.shopee.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Category findByName(String name);

    <T> Set<T> findAllBy(Class<T> type);

    <T> Optional<T> findByName(String name, Class<T> type);

    <T> Optional<T> findById(Long id, Class<T> type);

    
}
