package com.sookmyung.campus_match.exception;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.exception.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 전역 예외 처리기
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 검증 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<ValidationErrorResponse>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        List<ValidationErrorResponse.FieldError> fieldErrors = errors.entrySet().stream()
                .map(entry -> ValidationErrorResponse.FieldError.builder()
                        .field(entry.getKey())
                        .message(entry.getValue())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        ValidationErrorResponse validationErrors = ValidationErrorResponse.builder()
                .errors(fieldErrors)
                .build();

        ApiEnvelope<ValidationErrorResponse> response = ApiEnvelope.<ValidationErrorResponse>builder()
                .success(false)
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .data(validationErrors)
                .build();

        log.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * IllegalArgumentException 처리 (페이징/정렬/검색 파라미터 오류)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INVALID_PARAMETER")
                .message(ex.getMessage())
                .build();
        
        log.warn("Invalid parameter: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 리소스 없음 예외 처리
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 중복 리소스 예외 처리
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("DUPLICATE_RESOURCE")
                .message(ex.getMessage())
                .build();
        
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 권한 부족 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("ACCESS_DENIED")
                .message(ex.getMessage())
                .build();
        
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 인증 실패 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("AUTHENTICATION_FAILED")
                .message(ex.getMessage())
                .build();
        
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 데이터 무결성 위반 예외 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("DATA_INTEGRITY_VIOLATION")
                .message("Data integrity violation occurred")
                .build();
        
        log.error("Data integrity violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * API 예외 처리
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleApiException(
            ApiException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        
        log.warn("API exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> handleGenericException(
            Exception ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .build();
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
