package com.sookmyung.campus_match.exception;

import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.exception.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 로직 예외
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
        log.warn("API Exception: {}", e.getMessage());
        
        HttpStatus status;
        
        // 에러 코드별 상태 코드 분기
        switch (e.getErrorCode()) {
            case USER_ALREADY_EXISTS:
            case DUPLICATE_RESOURCE:
                status = HttpStatus.CONFLICT;
                break;
            case UNAUTHORIZED:
                status = HttpStatus.UNAUTHORIZED;
                break;
            case FORBIDDEN:
                status = HttpStatus.FORBIDDEN;
                break;
            case USER_NOT_FOUND:
            case POST_NOT_FOUND:
            case COMMENT_NOT_FOUND:
            case MESSAGE_NOT_FOUND:
            case TEAM_NOT_FOUND:
                status = HttpStatus.NOT_FOUND;
                break;
            default:
                status = HttpStatus.BAD_REQUEST;
                break;
        }
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(e.getErrorCode().getCode(), e.getMessage()));
    }

    // 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationErrorResponse>> handleValidationException(
            MethodArgumentNotValidException e) {
        
        List<ValidationErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ValidationErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : null)
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .errors(fieldErrors)
                .build();

        log.warn("Validation Exception: {}", fieldErrors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.VALIDATION_ERROR.getCode(), 
                        "입력값 검증에 실패했습니다.", errorResponse));
    }

    // 타입 변환 예외
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        
        String message = String.format("'%s' 값 '%s'을(를) %s 타입으로 변환할 수 없습니다.", 
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        
        log.warn("Type Mismatch Exception: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.VALIDATION_ERROR.getCode(), message));
    }

    // 인증 예외
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(ErrorCode.UNAUTHORIZED.getCode(), "인증이 필요합니다."));
    }

    // 권한 예외
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access Denied Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(ErrorCode.FORBIDDEN.getCode(), "접근 권한이 없습니다."));
    }

    // DB 제약 위반 예외
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("Data Integrity Violation Exception: {}", e.getMessage());
        
        String message;
        HttpStatus status;
        
        if (e.getMessage().contains("Duplicate entry")) {
            // 중복 데이터 예외
            if (e.getMessage().contains("users.email")) {
                message = "이미 존재하는 이메일입니다.";
            } else if (e.getMessage().contains("users.student_id")) {
                message = "이미 존재하는 학번입니다.";
            } else {
                message = "이미 존재하는 데이터입니다.";
            }
            status = HttpStatus.CONFLICT;
        } else if (e.getMessage().contains("cannot be null")) {
            // NOT NULL 제약 위반 - 구체적인 필드명 추출
            String fieldName = extractFieldNameFromErrorMessage(e.getMessage());
            if (fieldName != null) {
                message = String.format("필수 필드 '%s'가 누락되었습니다.", fieldName);
            } else {
                message = "필수 필드가 누락되었습니다.";
            }
            status = HttpStatus.BAD_REQUEST;
        } else {
            // 기타 제약 위반
            message = "데이터 제약 조건 위반";
            status = HttpStatus.BAD_REQUEST;
        }
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(ErrorCode.VALIDATION_ERROR.getCode(), message));
    }



    // 기타 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected Exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), 
                        "서버 내부 오류가 발생했습니다."));
    }

    // DB 오류 메시지에서 필드명 추출 헬퍼 메서드
    private String extractFieldNameFromErrorMessage(String errorMessage) {
        // 예: "Column 'operator' cannot be null" -> "operator"
        if (errorMessage.contains("Column '") && errorMessage.contains("' cannot be null")) {
            int startIndex = errorMessage.indexOf("Column '") + 8;
            int endIndex = errorMessage.indexOf("' cannot be null");
            if (startIndex > 7 && endIndex > startIndex) {
                return errorMessage.substring(startIndex, endIndex);
            }
        }
        return null;
    }
}
