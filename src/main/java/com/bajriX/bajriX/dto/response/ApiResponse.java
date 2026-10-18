package com.bajriX.bajriX.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Consistent API response envelope.
 * Every endpoint returns this shape so the frontend never guesses the format.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    T data,
    String message,
    Map<String, String> errors
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, null);
    }

    public static <T> ApiResponse<T> validationError(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, null, message, errors);
    }
}
