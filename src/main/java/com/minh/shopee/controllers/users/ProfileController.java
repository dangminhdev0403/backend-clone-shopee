package com.minh.shopee.controllers.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.services.utils.files.UploadHelper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "FileController")

@RequestMapping("/api/v1/profile")

public class ProfileController {

    private final UploadHelper uploadHelper;

    // Endpoint để upload file
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            String fileUrl = uploadHelper.handleSaveUploadFile(file, "merged");
            if (fileUrl == null) {
                return ResponseEntity.badRequest()
                        .body("Không thể upload file. Vui lòng kiểm tra lại.");
            }
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            log.error("Lỗi khi upload file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server khi upload file");
        }
    }

    // Endpoint để xóa file
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            boolean deleted = uploadHelper.handleDeleteFile(fileName, "merged");
            if (!deleted) {
                return ResponseEntity.badRequest()
                        .body("Không thể xóa file. File không tồn tại hoặc URL không hợp lệ.");
            }
            return ResponseEntity.ok("Xóa file thành công");
        } catch (Exception e) {
            log.error("Lỗi khi xóa file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server khi xóa file");
        }
    }
}
