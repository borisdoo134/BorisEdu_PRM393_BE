package com.borisedu.borisedu.utils;



import com.borisedu.borisedu.dto.response.ApiResponse;
import com.borisedu.borisedu.dto.response.LoginResponse;
import com.borisedu.borisedu.dto.response.UserResponse;

import java.time.Instant;

public class BuildResponse {
    public static <T> ApiResponse<T> buildApiResponse(Integer status, Object message, String errorMessage, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errorMessage(errorMessage)
                .data(data)
                .build();
    }

    public static LoginResponse buildLoginResponse(UserResponse userResponse, String accessToken, Instant expireAt, String refreshToken) {
        return LoginResponse.builder()
                .user(userResponse)
                .accessToken(accessToken)
                .expireAt(expireAt)
                .refreshToken(refreshToken)
                .build();
    }

}
