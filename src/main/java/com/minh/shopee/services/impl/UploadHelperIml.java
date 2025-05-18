package com.minh.shopee.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.services.utils.files.UploadHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "UploadHelper")
public class UploadHelperIml implements UploadHelper {

    @Value("${file.upload.dir}")
    private String uploadDir;

    public String handleSaveUploadFile(MultipartFile file, String forder) {
        if (file.isEmpty()) {
            log.warn("Tệp upload trống.");
            return null;
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir + "/" + forder).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Tạo tên file an toàn và duy nhất
            String originalFileName = file.getOriginalFilename();
            String sanitizedFileName = originalFileName == null ? ""
                    : originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String uniqueFileName = System.currentTimeMillis() + "-" + sanitizedFileName;

            // Ghi file lên server
            Path targetLocation = uploadPath.resolve(uniqueFileName);
            file.transferTo(targetLocation.toFile());

            // Tạo URL tố tập file
            String fullFileUrl = uploadPath.toUri().toString() + "/" + uniqueFileName;
            log.info("Tệp đã được lưu: {}", fullFileUrl);

            return fullFileUrl;
        } catch (IOException e) {
            log.error("Lỗi khi lưu file: {}", e.getMessage());
            return null;
        }
    }

    public boolean handleDeleteFile(String fileName, String forder) {
        if (fileName == null || fileName.isEmpty()) {
            log.warn(" file rỗng hoặc null.");
            return false;
        }
        try {
            Path uploadPath = Paths.get(uploadDir + "/" + forder).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName);
            File file = filePath.toFile();

            if (file.exists()) {
                return deleteFile(filePath);
            } else {
                log.warn("File không tồn tại: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("Lỗi khi xóa file: {}", e.getMessage());
            return false;
        }
    }

    private boolean deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
            log.info("Đã xóa file: {}", filePath);
            return true;
        } catch (IOException ex) {
            log.error("Không thể xóa file: {}", filePath, ex);
            return false;
        }
    }
}
