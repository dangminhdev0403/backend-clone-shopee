package com.minh.shopee.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface LocationService {
    void addAllLocation(MultipartFile locationFile) throws IOException;

    void deleteAllLocations();
}
