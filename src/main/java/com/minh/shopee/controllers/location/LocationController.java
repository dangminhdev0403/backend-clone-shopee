package com.minh.shopee.controllers.location;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.services.LocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/locations")
    public ResponseEntity<String> addLocation(
            @org.springframework.web.bind.annotation.RequestParam("locationFile") MultipartFile locationFile)
            throws IOException {
        this.locationService.addLocation(locationFile);
        return ResponseEntity.ok().body("Đã thêm đủ danh sách địa chỉ");

    }

}
