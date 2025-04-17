package com.minh.shopee.services.utils.error;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.minh.shopee.models.response.ResponseData;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j(topic = "GlobalExceptionHandler")
public class GlobalExceptionHandler {

    private ResponseData<Object> createResponseData(int status, String error, Object message) {
        return ResponseData.<Object>builder()
                .status(status)
                .error(error)
                .message(message)
                .build();
    }

    // ❌ Exception chung chung
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseData<Object>> commonException(Exception ex, HttpServletRequest request) {
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = "Lỗi hệ thống";

        // 📌 Log thêm URL gây lỗi và exception stacktrace
        log.error("❌ [COMMON EXCEPTION] URL: {} | Message: {}",
                request.getRequestURL(), ex.getMessage(), ex);

        ResponseData<Object> data = createResponseData(statusCode, error, ex);

        return ResponseEntity.status(statusCode).body(data);
    }

    // ❌ Không tìm thấy endpoint
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ResponseData<Object>> noResourceFoundException(NoResourceFoundException ex,
            HttpServletRequest request) {

        int statusCode = HttpStatus.NOT_FOUND.value(); // nên là 404 thay vì 500
        String error = "Endpoint không tồn tại";
        String url = request.getRequestURL().toString();
        String message = "URL " + url + " không tồn tại";

        // 📌 Log rõ lý do 404
        log.warn("⚠️ [404 NOT FOUND] URL: {} | Message: {}", url, ex.getMessage());

        ResponseData<Object> data = createResponseData(statusCode, error, message);

        return ResponseEntity.status(statusCode).body(data);
    }

    // ❌ Không chấp nhận phương thức
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseData<Object>> httpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        int statusCode = HttpStatus.METHOD_NOT_ALLOWED.value(); // nên là 404 thay vì 500
        String error = "Method không hỗ trợ";
        String requestMethod = request.getMethod();
        String message = "Phương thức " + requestMethod + " không hỗ trợ";

        // 📌 Log rõ lý do 404
        log.warn("⚠️ [405 NOT ALLOWED] URL: {} | Message: {}", requestMethod, ex.getMessage());

        ResponseData<Object> data = createResponseData(statusCode, error, message);

        return ResponseEntity.status(statusCode).body(data);
    }

    // ❌ lỗi validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Object>> methodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        int statusCode = HttpStatus.BAD_REQUEST.value();
        String error = "Lỗi validation";
        BindingResult result = ex.getBindingResult();

        List<FieldError> fieldErrors = result.getFieldErrors();

        List<Map<String, String>> message = fieldErrors.stream()
                .map(fieldError -> Map.of(fieldError.getField(), fieldError.getDefaultMessage())).toList();
        // 📌 Log rõ lý do 404
        log.warn("⚠️ [400 VALIDATION ERROR]   Message: {}", message);

        ResponseData<Object> data = createResponseData(statusCode, error, message);

        return ResponseEntity.status(statusCode).body(data);
    }

    @ExceptionHandler(value = DuplicateException.class)
    public ResponseEntity<ResponseData<Object>> handleDuplicateException(
            DuplicateException ex, HttpServletRequest request) {

        int statusCode = HttpStatus.CONFLICT.value(); // 409
        String error = "Trùng dữ liệu";
        String message = String.format("%s %s", ex.getFieldName(), ex.getMessage());

        log.warn("⚠️ [409 DUPLICATE DATA] Field: {} | URL: {} | Message: {}",
                ex.getFieldName(), request.getRequestURL(), ex.getMessage());

        ResponseData<Object> data = createResponseData(statusCode, error, message);

        return ResponseEntity.status(statusCode).body(data);
    }

}
