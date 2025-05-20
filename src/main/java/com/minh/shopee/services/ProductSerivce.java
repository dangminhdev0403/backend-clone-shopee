package com.minh.shopee.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.ProductReqDTO;
import com.minh.shopee.domain.dto.response.products.ProductResDTO;
import com.minh.shopee.domain.model.Product;

public interface ProductSerivce {

    <T> Set<T> getAllProducts(Class<T> type);

    Page<Product> getAllProducts(Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    ProductResDTO createAProduct(ProductReqDTO productDTO, List<MultipartFile> imageProduct);

    void createListProduct(MultipartFile file);
}
