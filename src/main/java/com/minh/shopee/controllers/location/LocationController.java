package com.minh.shopee.controllers.location;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LocationController {

    @PostMapping("/locations")
    public ResponseEntity<String> addLocation(@RequestBody String entity) {
        

        return ResponseEntity.ok().body("Đã thêm đủ danh sách địa chỉ");

    }

}
