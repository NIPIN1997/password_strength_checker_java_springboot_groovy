package com.projectsbynipin.passwordruleengine.utils;

import com.projectsbynipin.passwordruleengine.dto.ApiResponse;
import com.projectsbynipin.passwordruleengine.enums.Status;

import java.time.LocalDateTime;

public class ApiResponseCreator {

    private ApiResponseCreator() {
    }

    public static <T> ApiResponse<T> success(boolean passwordStrong, String message, T data) {
        return ApiResponse.<T>builder()
                .status(Status.SUCCESS)
                .message(message)
                .passwordStrong(passwordStrong)
                .data(data)
                .time(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> success(boolean passwordStrong, String message) {
        return ApiResponse.<Void>builder()
                .status(Status.SUCCESS)
                .message(message)
                .passwordStrong(passwordStrong)
                .time(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .status(Status.ERROR)
                .message(message)
                .time(LocalDateTime.now())
                .build();
    }
}
