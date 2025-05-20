package com.minh.shopee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface GenericRepositoryCustom<T> {
    <R> Page<R> findAll(Specification<T> spec, Pageable pageable, Class<R> projection);

}
