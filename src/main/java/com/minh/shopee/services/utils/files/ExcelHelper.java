package com.minh.shopee.services.utils.files;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelHelper {
    void isExcelFile(MultipartFile file);

    LocationData readExcelLocationFile(MultipartFile file) throws IOException;

}
