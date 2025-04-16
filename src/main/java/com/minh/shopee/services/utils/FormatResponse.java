package com.minh.shopee.services.utils;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.minh.shopee.models.anotation.ApiDescription;
import com.minh.shopee.models.response.ResponseData;

import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("rawtypes")
@RestControllerAdvice
public class FormatResponse implements ResponseBodyAdvice {

    @Override
    public boolean supports(@SuppressWarnings("null") MethodParameter returnType,
            @SuppressWarnings("null") Class converterType) {

        return true;
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // ! Lấy status code
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int statusCode = httpResponse.getStatus();

        // ! kiểm tra là lỗi hay thành công
        if (body instanceof String || statusCode >= 400) {
            return body;

        }
        Method method = returnType.getMethod();
        ApiDescription apiDescription = null;

        if (method != null) {
            apiDescription = method.getAnnotation(ApiDescription.class);
        }
        String messageApi = apiDescription == null ? "CALL API THÀNH CÔNG"
                : apiDescription.value();

        return ResponseData.<Object>builder().status(statusCode).message(messageApi).data(body).build();
    }

}
