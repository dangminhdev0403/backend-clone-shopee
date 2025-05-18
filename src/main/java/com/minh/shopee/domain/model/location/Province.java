package com.minh.shopee.domain.model.location;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minh.shopee.domain.base.BaseLocation;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provinces")
@Getter
@Setter
public class Province extends BaseLocation {

    @OneToMany(mappedBy = "province")
    @JsonIgnore
    List<District> districts;
}
