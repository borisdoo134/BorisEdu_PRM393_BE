
package com.borisedu.borisedu.utils.validation;

import com.borisedu.borisedu.dto.response.ApiResponse;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class FormatApiResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpServletResponse.getStatus();
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(status);
        if (body instanceof String || body instanceof ApiResponse) {
            return body;
        }
        if (status >= 400) {
            return body;
        } else {
            apiResponse.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            apiResponse.setMessage(message != null ? message.value() : "Thành công!");
        }
        return apiResponse;
    }
}