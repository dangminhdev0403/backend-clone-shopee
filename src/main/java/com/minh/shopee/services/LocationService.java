package com.minh.shopee.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface LocationService {
    void addAllLocation(MultipartFile locationFile) throws IOException;

    void deleteAllLocations();

    <T> List<T> getListProvinces(Class<T> type);

    <T> List<T> getListDistricts(Long provinceId, Class<T> type);

    <T> List<T> getListWards(Long provinceId, Class<T> type);

}
