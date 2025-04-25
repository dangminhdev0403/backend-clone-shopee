package com.minh.shopee.services.utils.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public class AppException extends RuntimeException {
    private final int status;
    private final String error;
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

}
