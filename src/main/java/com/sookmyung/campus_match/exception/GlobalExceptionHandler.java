package com.sookmyung.campus_match.exception;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.exception.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

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
     * ResponseStatusException 처리 (권한 없음, 리소스 없음 등)
     */
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleResponseStatusException(
            org.springframework.web.server.ResponseStatusException ex, WebRequest request) {
        
        String code;
        switch (ex.getStatusCode()) {
            case FORBIDDEN:
                code = "FORBIDDEN";
                break;
            case NOT_FOUND:
                code = "RESOURCE_NOT_FOUND";
                break;
            case BAD_REQUEST:
                code = "BAD_REQUEST";
                break;
            default:
                code = "ERROR";
        }
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code(code)
                .message(ex.getReason())
                .build();
        
        log.warn("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
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
     * 숫자 형식 변환 오류 처리
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleNumberFormatException(
            NumberFormatException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INVALID_NUMBER_FORMAT")
                .message("Invalid number format")
                .build();
        
        log.warn("Number format error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 메서드 인자 타입 불일치 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INVALID_PARAMETER_TYPE")
                .message("Invalid parameter type: " + ex.getName())
                .build();
        
        log.warn("Parameter type mismatch: {} - {}", ex.getName(), ex.getValue());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 필수 파라미터 누락 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("MISSING_PARAMETER")
                .message("Missing required parameter: " + ex.getParameterName())
                .build();
        
        log.warn("Missing parameter: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 지원하지 않는 HTTP 메서드 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("METHOD_NOT_ALLOWED")
                .message("Method not allowed: " + ex.getMethod())
                .build();
        
        log.warn("Method not allowed: {}", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 핸들러를 찾을 수 없음 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("ENDPOINT_NOT_FOUND")
                .message("Endpoint not found: " + ex.getRequestURL())
                .build();
        
        log.warn("Endpoint not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
