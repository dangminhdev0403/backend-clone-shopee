package com.minh.shopee.services.utils.files;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.model.Category;

public interface ExcelHelper {
    void isExcelFile(MultipartFile file);

    LocationData readExcelLocationFile(MultipartFile file) throws IOException;

    List<Category> readExcelCategoryFile(MultipartFile file) throws IOException;
}
