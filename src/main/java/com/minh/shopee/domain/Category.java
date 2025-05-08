package com.minh.shopee.domain;

import java.util.List;

import com.minh.shopee.domain.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
