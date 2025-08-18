package com.sookmyung.campus_match.dto.common;

import lombok.*;

import java.time.Instant;

/**
 * API 응답 래퍼 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApiEnvelope<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;
    private Instant timestamp;

    /**
     * 성공 응답 생성 (201 Created)
     */
    public static <T> ApiEnvelope<T> created(T data) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .code("OK")
                .message("success")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 성공 응답 생성 (200 OK)
     */
    public static <T> ApiEnvelope<T> success(T data) {
        return ApiEnvelope.<T>builder()
                .success(true)
                .code("OK")
                .message("success")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    public static <T> ApiEnvelope<T> okMessage() {
        return ApiEnvelope.<T>builder()
                .success(true)
                .code("OK")
                .message("success")
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 에러 응답 생성
     */
    public static <T> ApiEnvelope<T> error(String code, String message) {
        return ApiEnvelope.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}




