package com.minh.shopee.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.ProductReqDTO;
import com.minh.shopee.domain.dto.response.products.ProductResDTO;
import com.minh.shopee.domain.model.Category;
import com.minh.shopee.domain.model.Product;
import com.minh.shopee.domain.model.ProductImage;
import com.minh.shopee.repository.ProductImageRepository;
import com.minh.shopee.repository.ProductRepository;
import com.minh.shopee.services.ProductSerivce;
import com.minh.shopee.services.utils.files.UploadCloud;
import com.minh.shopee.services.utils.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ProductService")
public class ProductServiceImpl implements ProductSerivce {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final UploadCloud uploadCloud;

    @Override
    public <T> Set<T> getAllProducts(Class<T> type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProducts'");
    }

    @Override
    public ProductResDTO createAProduct(ProductReqDTO productDTO, List<MultipartFile> imagesProduct) {
        Category category = new Category();
        category.setId(productDTO.getCategoryId());
        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .category(category)
                .build();

        productRepository.save(product);

        if (imagesProduct != null && !imagesProduct.isEmpty()) {

            List<ProductImage> productImages = imagesProduct.stream()
                    .map(image -> {
                        String imageUrl = this.mapToProductImage(image, product).getImageUrl();
                        return ProductImage.builder()
                                .imageUrl(imageUrl)
                                .product(product)
                                .build();
                    })
                    .toList();

            product.setImages(productImages);
            productImageRepository.saveAll(productImages);

        }

        return ProductMapper.toProductResDTO(product);

    }

    private ProductImage mapToProductImage(MultipartFile image, Product product) {
        try {
            String imageUrl = uploadCloud.handleSaveUploadFile(image, "products");
            return ProductImage.builder()
                    .imageUrl(imageUrl)
                    .product(product)
                    .build();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
