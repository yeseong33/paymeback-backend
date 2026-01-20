package com.paymeback.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private Long timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .message(message)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }

    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
            .success(true)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
            .success(true)
            .message(message)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }
}