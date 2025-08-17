package com.sookmyung.campus_match.dto.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    
    /** 필드별 오류 메시지 배열 */
    private List<FieldError> errors;
    
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String rejectedValue;
        private String message;
    }
}


