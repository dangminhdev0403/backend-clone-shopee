package com.minh.shopee.domain.dto.response.products;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductNamePriceDTO {
    private String name;
    private BigDecimal price;

    public ProductNamePriceDTO(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    // getter, setter...
}