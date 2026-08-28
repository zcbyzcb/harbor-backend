package com.harbor.hotel.api.web;

public record ApiResponse<T>(
        String code, String message, T data, String requestId, String traceId) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                "SUCCESS",
                "操作成功",
                data,
                org.slf4j.MDC.get("requestId"),
                org.slf4j.MDC.get("traceId"));
    }
}
