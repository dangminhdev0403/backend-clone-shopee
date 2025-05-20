package com.minh.shopee.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.minh.shopee.services.utils.files.ExcelHelper;
import com.minh.shopee.services.utils.files.UploadCloud;
import com.minh.shopee.services.utils.files.data.ProductData;
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
    private final ExcelHelper excelHelper;

    @Override
    public <T> Set<T> getAllProducts(Class<T> type) {
       
        return productRepository.findAllBy(type);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
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

    @Override
    public void createListProduct(MultipartFile file) {
        try {
            ProductData productData = this.excelHelper.readExcelProductFile(file);
            List<Product> listProducts = productData.getListProducts();
            List<ProductImage> listProductImages = productData.getListProductImages();
            if (listProducts != null && !listProducts.isEmpty()) {
                log.info("Save List product: {}", listProducts);
                productRepository.saveAll(listProducts);
            }
            if (listProductImages != null && !listProductImages.isEmpty()) {
                log.info("Save List product image: {}", listProductImages);
                productImageRepository.saveAll(listProductImages);
            }
        } catch (IOException e) {
            log.error("Error reading excel file: {}", e.getMessage());
            e.printStackTrace();
        }

    }

}
