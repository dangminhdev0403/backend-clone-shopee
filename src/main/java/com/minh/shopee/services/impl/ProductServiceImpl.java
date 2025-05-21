package com.minh.shopee.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.FiltersProduct;
import com.minh.shopee.domain.dto.request.ProductReqDTO;
import com.minh.shopee.domain.dto.response.products.ProductImageDTO;
import com.minh.shopee.domain.dto.response.products.ProductProjection;
import com.minh.shopee.domain.dto.response.products.ProductResDTO;
import com.minh.shopee.domain.model.Category;
import com.minh.shopee.domain.model.Product;
import com.minh.shopee.domain.model.ProductImage;
import com.minh.shopee.domain.specification.ProductImageSpecs;
import com.minh.shopee.domain.specification.ProductSpecification;
import com.minh.shopee.repository.GenericRepositoryCustom;
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
    private final GenericRepositoryCustom<Product> productCustomRepo;
    private final GenericRepositoryCustom<ProductImage> productImageCustomRepo;

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

    @Override
    public Page<ProductResDTO> searchProducts(String keyword, FiltersProduct filter, Pageable pageable) {

        if (filter != null) {
            if (filter.getMinPrice().isPresent() && filter.getMaxPrice().isPresent()) {
                BigDecimal minPrice = new BigDecimal(filter.getMinPrice().get());
                BigDecimal maxPrice = new BigDecimal(filter.getMaxPrice().get());
                ProductSpecification.hasPriceRange(minPrice, maxPrice);
            }
            if (filter.getStock().isPresent()) {
                Integer stock = Integer.parseInt(filter.getStock().get());
                ProductSpecification.hasStock(stock);
            }
            if (filter.getCategoryId().isPresent()) {
                Long categoryId = Long.parseLong(filter.getCategoryId().get());
                ProductSpecification.hasCategoryId(categoryId);
            }
        }
        log.info("getingg list product width filter: {}");
        Page<ProductProjection> products = this.productCustomRepo.findAll(ProductSpecification.hasName(keyword),
                pageable,
                ProductProjection.class);
        List<ProductProjection> productList = products.getContent();

        // Map từng Product sang ProductResDTO, tìm image đầu tiên theo productId
        log.info("Mapping product to ProductResDTO, find image first by productId");
        List<ProductResDTO> dtoList = productList.stream()
                .map(product -> {
                    Optional<ProductImageDTO> firstImageOpt = this.productImageCustomRepo.findOne(
                            ProductImageSpecs.findFirstImageByProductId(product.getId()),
                            ProductImageDTO.class);

                    String imageUrl = firstImageOpt.map(ProductImageDTO::getImageUrl).orElse(null);
                    return ProductResDTO.builder()
                            .name(product.getName())
                            .price(product.getPrice())
                            .imageUrl(imageUrl)
                            .build();
                }).toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                products.getTotalElements());
    }
}
