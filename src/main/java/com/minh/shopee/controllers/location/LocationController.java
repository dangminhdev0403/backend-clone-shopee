package com.minh.shopee.controllers.location;

import java.io.IOException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.dto.response.LocationDTO;
import com.minh.shopee.services.LocationService;
import com.minh.shopee.services.utils.error.AppException;

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
        this.locationService.addAllLocation(locationFile);
        return ResponseEntity.ok().body("Đã thêm đủ danh sách địa chỉ");

    }

    @DeleteMapping("/locations")
    public ResponseEntity<String> deleteLocation() {
        this.locationService.deleteAllLocations();
        return ResponseEntity.ok().body("Đã xóa hết địa chỉ");
    }

    @GetMapping("/provinces")
    public ResponseEntity<Page<LocationDTO>> getAllProvinces(@PageableDefault(page = 0, size = 99) Pageable pageable) {
        Page<LocationDTO> provinces = this.locationService.getListProvinces(LocationDTO.class, pageable);
        return ResponseEntity.ok().body(provinces);
    }

    @GetMapping("/districts")
    public ResponseEntity<Page<LocationDTO>> getListDistrictsWithProvince(Optional<String> provinceId,
            @PageableDefault(page = 0, size = 99) Pageable pageable) {
        if (provinceId.isEmpty()) {
            throw new AppException(400, "Param  is required", "provinceId is required");
        }

        Page<LocationDTO> districts = this.locationService.getListDistricts(Long.parseLong(provinceId.get()),
                LocationDTO.class, pageable);
        return ResponseEntity.ok().body(districts);

    }

    @GetMapping("/wards")
    public ResponseEntity<Page<LocationDTO>> getListWardsWithDistrict(Optional<String> districtId,
            @PageableDefault(page = 0, size = 99) Pageable pageable) {
        if (districtId.isEmpty()) {
            throw new AppException(400, "Param  is required", "districtId is required");
        }
        Page<LocationDTO> wards = this.locationService.getListWards(Long.parseLong(districtId.get()),
                LocationDTO.class, pageable);
        return ResponseEntity.ok().body(wards);

    }

}
