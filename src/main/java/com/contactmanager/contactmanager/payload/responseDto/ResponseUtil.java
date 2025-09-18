package com.contactmanager.contactmanager.payload.responseDto;

import java.time.Instant;

public class ResponseUtil {

    public static ApiResponse success(Integer statusCode, String message, Object data, Object metadata) {
        return ApiResponse.builder()
                .status("success")
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
    }
    public static ApiResponse error(Integer statusCode, String message, Object errors, Object data, Object metadata) {
        return ApiResponse.builder()
                .status("error")
                .message(message)
                .statusCode(statusCode)
                .errors(errors)
                .data(data)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
    }

}

