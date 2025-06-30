package com.minh.shopee.services;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.model.location.District;
import com.minh.shopee.domain.model.location.Province;
import com.minh.shopee.domain.model.location.Ward;

public interface LocationService {
    void addAllLocation(MultipartFile locationFile) throws IOException;

    void deleteAllLocations();

    <T> Page<T> getListProvinces(Class<T> type, Pageable pageable);

    <T> Page<T> getListDistricts(Long provinceId, Class<T> type, Pageable pageable);

    <T> Page<T> getListWards(Long provinceId, Class<T> type, Pageable pageable);

    Province getProvinceById(Long provinceId);

    District getDistrictById(Long districtId);

    Ward getWardById(Long wardId);

}
