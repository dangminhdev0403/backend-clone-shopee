package com.minh.shopee.services.utils.files;

import java.util.List;
import java.util.Set;

import com.minh.shopee.domain.model.location.District;
import com.minh.shopee.domain.model.location.Province;
import com.minh.shopee.domain.model.location.Ward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class LocationData {
    private Set<Province> provinces;
    private Set<District> districts;
    private Set<Ward> wards;
}
