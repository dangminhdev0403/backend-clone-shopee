package com.minh.shopee.services;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.ProductReqDTO;
import com.minh.shopee.domain.dto.response.products.ProductResDTO;

public interface ProductSerivce {

    <T> Set<T> getAllProducts(Class<T> type);

    ProductResDTO createAProduct(ProductReqDTO productDTO, List<MultipartFile> imageProduct);
}
