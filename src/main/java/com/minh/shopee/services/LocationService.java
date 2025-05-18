package com.minh.shopee.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface LocationService {
    void addLocation(MultipartFile locationFile) throws IOException;
}
