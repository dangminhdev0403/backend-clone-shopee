package com.minh.shopee.services.utils.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.minh.shopee.models.response.ResponseData;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j(topic = "GlobalException")

public class GlobalExceptionHandler {

    private ResponseData<Object> createResponseData(int status, String error, Object message) {

        return ResponseData.<Object>builder().status(status).error(error).message(message).build();

    }

    // !Ngoại lệ chung
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseData<Object>> commonException(Exception ex) {

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = "Một lỗi với vẩn nào đó chưa fix";

        ResponseData<Object> data = createResponseData(statusCode, error, ex);

        return ResponseEntity.status(statusCode).body(data);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ResponseData<Object>> noResourceFoundException(NoResourceFoundException ex,
            HttpServletRequest request) {

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = "Enpoint không tồn tại";
        String url = request.getRequestURL().toString();
        String message = "URL " + url + " không tồn tại";

        ResponseData<Object> data = createResponseData(statusCode, error, message);

        return ResponseEntity.status(statusCode).body(data);
    }
}
