package com.minh.shopee.controllers.admin;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.request.ProductReqDTO;
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

    @GetMapping("")
    public ResponseEntity<Set<ProductResDTO>> getAllProducts() {
        Set<ProductResDTO> products = productSerivce.getAllProducts(ProductResDTO.class);
        return ResponseEntity.ok(products);
    }
    

}
