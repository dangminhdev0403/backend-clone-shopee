package com.minh.shopee.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.constant.QuantityAction;
import com.minh.shopee.domain.dto.request.AddProductDTO;
import com.minh.shopee.domain.dto.request.ProductReqDTO;
import com.minh.shopee.domain.dto.request.filters.FiltersProduct;
import com.minh.shopee.domain.dto.request.filters.SortFilter;
import com.minh.shopee.domain.dto.response.products.ProductImageDTO;
import com.minh.shopee.domain.dto.response.products.ProductProjection;
import com.minh.shopee.domain.dto.response.products.ProductResDTO;
import com.minh.shopee.domain.model.Cart;
import com.minh.shopee.domain.model.CartDetail;
import com.minh.shopee.domain.model.Category;
import com.minh.shopee.domain.model.Product;
import com.minh.shopee.domain.model.ProductImage;
import com.minh.shopee.domain.model.User;
import com.minh.shopee.domain.specification.ProductImageSpecs;
import com.minh.shopee.domain.specification.ProductSpecification;
import com.minh.shopee.repository.CartDetailRepository;
import com.minh.shopee.repository.CartRepository;
import com.minh.shopee.repository.GenericRepositoryCustom;
import com.minh.shopee.repository.ProductImageRepository;
import com.minh.shopee.repository.ProductRepository;
import com.minh.shopee.services.ProductSerivce;
import com.minh.shopee.services.utils.error.AppException;
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
    private final CartDetailRepository cartDetailRepository;
    private final CartRepository cartRepository;

    @Override
    public <T> Set<T> getAllProducts(Class<T> type) {

        return productRepository.findAllBy(type);
    }

    @Override
    public Page<ProductResDTO> getAllProducts(Pageable pageable) {
        Page<ProductProjection> products = this.productRepository.findAllBy(pageable, ProductProjection.class);
        List<ProductProjection> productList = products.getContent();

        List<ProductResDTO> dtoList = productList.stream()
                .map(product -> {
                    Optional<ProductImageDTO> firstImageOpt = this.productImageCustomRepo.findOne(
                            ProductImageSpecs.findFirstImageByProductId(product.getId()),
                            ProductImageDTO.class);

                    String imageUrl = firstImageOpt.map(ProductImageDTO::getImageUrl).orElse(null);
                    return ProductResDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .imageUrl(imageUrl)
                            .stock(product.getStock())
                            .build();
                }).toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                products.getTotalElements());
    }

    @Override
    public Page<ProductResDTO> searchProducts(String keyword, FiltersProduct filter, SortFilter sortFilter,
            Pageable pageable) {
        pageable = applySortFromFilter(pageable, sortFilter);
        Specification<Product> spec = buildProductSpecification(keyword, filter);

        log.info("getingg list product width filter: {}");
        Page<ProductProjection> products = this.productCustomRepo.findAll(
                spec,
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
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .imageUrl(imageUrl)
                            .stock(product.getStock())
                            .build();
                }).toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                products.getTotalElements());
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

    private Pageable applySortFromFilter(Pageable pageable, SortFilter sortFilter) {
        log.info("Applying sort from filter: {}", sortFilter);
        if (sortFilter != null) {

            String sortBy = switch (sortFilter.getSortBy()) {
                case "ctime" -> "createdAt";
                default -> sortFilter.getSortBy();
            };

            String order = sortFilter.getOrder();

            Sort.Direction direction = Sort.Direction.fromString(order);
            Sort sort = Sort.by(direction, sortBy);

            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());

    }

    private Specification<Product> buildProductSpecification(String keyword, FiltersProduct filter) {
        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty()) {
            log.info("Searching product with keyword: {}", keyword);

            spec = spec.and(ProductSpecification.hasName(keyword));
        }
        if (filter != null) {
            if (filter.getMinPrice().isPresent() && filter.getMaxPrice().isPresent()) {
                BigDecimal minPrice = new BigDecimal(filter.getMinPrice().get());
                BigDecimal maxPrice = new BigDecimal(filter.getMaxPrice().get());
                spec = spec.and(ProductSpecification.hasPriceRange(minPrice, maxPrice));
            }
            if (filter.getStock().isPresent()) {
                Integer stock = Integer.parseInt(filter.getStock().get());
                spec = spec.and(ProductSpecification.hasStock(stock));
            }
            if (filter.getCategoryId().isPresent()) {
                Long categoryId = Long.parseLong(filter.getCategoryId().get());
                spec = spec.and(ProductSpecification.hasCategoryId(categoryId));
            }
        }
        return spec;
    }

    @Override
    public <T> T getProductById(long id, Class<T> type) {
        log.info("Fetching product by id: {} with projection type: {}", id, type.getSimpleName());
        Optional<T> product = this.productRepository.findById(id, type);
        if (product.isEmpty()) {
            log.error("Product not found with id: {}", id);
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Product not found",
                    "Product with id " + id + " not found");
        }
        return product.get();
    }

    @Override
    public void addProductToCart(AddProductDTO productReq, Long userId) {
        Product productDB = this.productRepository.findById(productReq.getProductId()).orElse(null);
        if (productDB == null) {
            log.error("Product not found with id: {}", productReq.getProductId());
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Product not found",
                    "Product with id " + productReq.getProductId() + " not found");
        }
        Cart isExistCart = this.cartRepository.findByUserId(userId).orElse(null);

        if (isExistCart == null) {
            log.info("Cart not found , create cart with user id: {}", userId);
            Cart cart = new Cart();
            User user = new User();
            user.setId(userId);
            cart.setUser(user);
            this.cartRepository.save(cart);

            CartDetail cartDetail = CartDetail.builder().quantity(productReq.getQuantity())
                    .product(productDB).cart(cart).build();
            cartDetailRepository.save(cartDetail);
            return;
        }

        CartDetail cartDetail = this.cartDetailRepository.findByCartIdAndProductId(isExistCart.getId(),
                productReq.getProductId());

        if (cartDetail != null) {
            if (productReq.getAction() == QuantityAction.INCREASE) {
                cartDetail.setQuantity(cartDetail.getQuantity() + productReq.getQuantity());
            } else if (productReq.getAction() == QuantityAction.DECREASE) {
                if (productReq.getQuantity() > cartDetail.getQuantity()) {
                    throw new AppException(HttpStatus.BAD_REQUEST.value(), "Quantity is not enough",
                            "Quantity is not enough");
                }
                if (productReq.getQuantity() - cartDetail.getQuantity() == 0) {
                    this.cartDetailRepository.delete(cartDetail);
                    return;
                }
                cartDetail.setQuantity(cartDetail.getQuantity() - productReq.getQuantity());
            }

            this.cartDetailRepository.save(cartDetail);
        } else {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Product not found in cart",
                    "Product with id " + productReq.getProductId() + " not found in cart");
        }
    }

}
