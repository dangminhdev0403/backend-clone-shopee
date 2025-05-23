package com.minh.shopee.controllers.admin;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.ProductReqDTO;
import com.minh.shopee.domain.dto.request.filters.FiltersProduct;
import com.minh.shopee.domain.dto.request.filters.SortFilter;
import com.minh.shopee.domain.dto.response.products.ProductResDTO;
import com.minh.shopee.services.ProductSerivce;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSerivce productSerivce;

    @PostMapping("")
    public ResponseEntity<ProductResDTO> createAProduct(@ModelAttribute @Valid ProductReqDTO productDTO,
            @RequestParam(value = "imageProduct", required = false) List<MultipartFile> imagesProduct) {
        ProductResDTO productCreate = productSerivce.createAProduct(productDTO, imagesProduct);

        URI location = URI.create("/api/v1/products");
        return ResponseEntity.created(location)
                .body(productCreate);
    }

    @PostMapping("/import")
    public ResponseEntity<String> createListProduct(
            @RequestParam(value = "fileProductExcel", required = false) MultipartFile file) {
        if (file != null) {
            this.productSerivce.createListProduct(file);
            return ResponseEntity.ok("Tạo danh sách sản phẩm thành công: ");
        }
        return ResponseEntity.ok("Tạo danh sách sản phẩm không thành công: ");
    }

    @GetMapping("")
    public ResponseEntity<Page<ProductResDTO>> getAllProducts( @PageableDefault(page = 0, size = 20) Pageable pageable) {

        Page<ProductResDTO> products = productSerivce.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResDTO>> searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            FiltersProduct filter, SortFilter sortFilter,
            Pageable pageable) {
                
        Page<ProductResDTO> products = productSerivce.searchProducts(keyword, filter, sortFilter, pageable);

        return ResponseEntity.ok(products);
    }

}
