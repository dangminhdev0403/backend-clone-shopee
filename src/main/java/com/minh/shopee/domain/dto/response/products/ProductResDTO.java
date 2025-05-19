package com.minh.shopee.domain.dto.response.products;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductResDTO {
    private String name;
    private String description;
    private BigDecimal price;

    private int stock;

}
