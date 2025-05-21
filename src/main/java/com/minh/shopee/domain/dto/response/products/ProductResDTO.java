package com.minh.shopee.domain.dto.response.products;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ProductResDTO {
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;

}
