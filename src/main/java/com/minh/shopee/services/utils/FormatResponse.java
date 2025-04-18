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
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@RestControllerAdvice
@Slf4j(topic = "FormatResponse")
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

        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int statusCode = httpResponse.getStatus();

        String methodName = returnType.getMethod() != null ? returnType.getMethod().getName() : "Unknown";
        String path = request.getURI().getPath();

        log.debug("Intercepting response for path: {}, method: {}, status: {}", path, methodName, statusCode);

        // Nếu là String hoặc status lỗi (>= 400) thì trả về nguyên body không format
        if (body instanceof String || statusCode >= 400) {
            log.debug("Skipping formatting for response. Status: {}, body type: {}", statusCode,
                    body.getClass().getSimpleName());
            return body;
        }

        // Lấy mô tả từ annotation (nếu có)
        Method method = returnType.getMethod();
        ApiDescription apiDescription = method != null ? method.getAnnotation(ApiDescription.class) : null;
        String messageApi = apiDescription != null ? apiDescription.value() : "CALL API THÀNH CÔNG";

        ResponseData<Object> wrappedResponse = ResponseData.<Object>builder()
                .status(statusCode)
                .message(messageApi)
                .data(body)
                .build();

        log.info("Formatted response for [{} {}] with message: {}", request.getMethod(), path, messageApi);

        return wrappedResponse;
    }
}
