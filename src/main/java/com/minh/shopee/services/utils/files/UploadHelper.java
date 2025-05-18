package com.minh.shopee.services.utils.files;

import org.springframework.web.multipart.MultipartFile;

public interface UploadHelper {
    String handleSaveUploadFile(MultipartFile file, String forder);

    boolean handleDeleteFile(String urlFile, String forder);
}