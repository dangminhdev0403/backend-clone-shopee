package com.minh.shopee.services.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.minh.shopee.repository.ProductRepository;
import com.minh.shopee.services.ProductSerivce;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductSerivce {
    private final ProductRepository productRepository;

    @Override
    public <T> Set<T> getAllProducts(Class<T> type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProducts'");
    }

}
