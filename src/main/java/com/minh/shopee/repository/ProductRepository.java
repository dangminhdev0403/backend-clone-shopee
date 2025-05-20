package com.minh.shopee.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.minh.shopee.domain.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>,
        GenericRepositoryCustom<Product> {

    <T> Set<T> findAllBy(Class<T> type);

    @SuppressWarnings("null")
    Page<Product> findAll(Pageable pageable);

    // <T> Page<T> findAll(Specification<Product> spec, Pageable pageable, Class<T> type);
}
