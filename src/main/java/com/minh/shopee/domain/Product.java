package com.minh.shopee.domain;

import java.math.BigDecimal;
import java.util.List;

import com.minh.shopee.domain.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@Table(name = "products")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private int stock;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    @ManyToOne
    private Category category;

}
