package com.sookmyung.campus_match.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 모든 API 응답을 감싸는 공통 래퍼.
 * - 날짜/시간은 ISO-8601(UTC) 문자열로 timestamp에 포함
 * - 실패 시 code/message를 활용(검증/비즈니스 코드 등)
 * - data가 없으면 제외(Non_NULL)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** 성공 여부 */
    private boolean success;

    /** 응답/오류 코드(e.g. "OK", "CREATED", "VALIDATION_ERROR", "BUSINESS_ERROR" 등) */
    private String code;

    /** 메시지(성공/오류 공통) */
    private String message;

    /** 실제 응답 데이터(없으면 직렬화에서 제외) */
    private T data;

    /** ISO-8601 UTC 타임스탬프 */
    private String timestamp;

    // --- Factory methods (성공) ---

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("OK")
                .message("success")
                .data(data)
                .timestamp(nowIso())
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return success(data);
    }

    public static <T> ApiResponse<T> okMessage(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("OK")
                .message(message)
                .timestamp(nowIso())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("CREATED")
                .message("created")
                .data(data)
                .timestamp(nowIso())
                .build();
    }

    // --- Factory methods (실패) ---

    public static <T> ApiResponse<T> fail(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(nowIso())
                .build();
    }

    public static <T> ApiResponse<T> fail(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .timestamp(nowIso())
                .build();
    }

    // --- Helper ---

    private static String nowIso() {
        return Instant.now().toString(); // ISO-8601 (e.g., 2025-08-10T12:34:56Z)
    }
}
