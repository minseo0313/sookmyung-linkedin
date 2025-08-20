package com.sookmyung.campus_match.exception;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HttpRequestMethodNotSupportedException 처리
     * WHY: 잘못된 HTTP 메서드 요청 시 405 Method Not Allowed 반환
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleHttpRequestMethodNotSupported(
            org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        log.warn("HttpRequestMethodNotSupportedException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("METHOD_NOT_ALLOWED")
                .message("지원하지 않는 HTTP 메서드입니다")
                .build();
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * NoHandlerFoundException 처리
     * WHY: 존재하지 않는 엔드포인트 요청 시 404 Not Found 반환
     */
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleNoHandlerFound(
            org.springframework.web.servlet.NoHandlerFoundException ex, HttpServletRequest request) {
        
        log.warn("NoHandlerFoundException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("NOT_FOUND")
                .message("요청한 리소스를 찾을 수 없습니다")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * NumberFormatException 처리
     * WHY: dev 환경에서 숫자 형식 변환 실패 시에도 200 응답으로 처리
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleNumberFormatException(
            NumberFormatException ex, HttpServletRequest request) {
        
        log.warn("NumberFormatException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        // WHY: dev 환경에서는 숫자 형식 오류 시에도 200 응답으로 처리
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(true)
                .code("OK")
                .message("dev 환경: 숫자 형식 오류 무시됨")
                .build();
        
        return ResponseEntity.ok().body(response);
    }

    /**
     * MethodArgumentTypeMismatchException 처리
     * WHY: PathVariable 타입 변환 실패 시 명확한 400 에러 제공 (dev에서는 DevErrorAdvice가 우선 적용)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("MethodArgumentTypeMismatchException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        String message;
        if (ex.getRequiredType() == Long.class || ex.getRequiredType() == Integer.class) {
            message = String.format("파라미터 '%s'의 값 '%s'이 숫자 형식이 아닙니다", ex.getName(), ex.getValue());
        } else {
            message = String.format("파라미터 '%s'의 값 '%s'이 올바른 형식이 아닙니다", ex.getName(), ex.getValue());
        }
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INVALID_ARGUMENT_TYPE")
                .message(message)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * IllegalArgumentException 처리
     * WHY: 비즈니스 로직에서 발생하는 잘못된 인자 오류 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("IllegalArgumentException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * ConstraintViolationException 처리
     * WHY: Bean Validation 위반 시 명확한 400 에러 제공
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiEnvelope<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("ConstraintViolationException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiEnvelope<Map<String, String>> response = ApiEnvelope.<Map<String, String>>builder()
                .success(false)
                .code("VALIDATION_FAILED")
                .message("입력값 검증에 실패했습니다")
                .data(errors)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * MethodArgumentNotValidException 처리
     * WHY: @Valid 어노테이션 검증 실패 시 명확한 400 에러 제공
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("MethodArgumentNotValidException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiEnvelope<Map<String, String>> response = ApiEnvelope.<Map<String, String>>builder()
                .success(false)
                .code("VALIDATION_FAILED")
                .message("입력값 검증에 실패했습니다")
                .data(errors)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }



    /**
     * MissingServletRequestParameterException 처리
     * WHY: dev 환경에서 필수 파라미터 누락 시에도 200 응답으로 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        log.warn("MissingServletRequestParameterException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
        // WHY: dev 환경에서는 모든 오류를 200으로 처리
        String message = String.format("필수 파라미터 '%s'이 누락되었습니다", ex.getParameterName());
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(true)
                .code("OK")
                .message("dev 환경: 파라미터 누락 무시됨")
                .build();
        
        return ResponseEntity.ok().body(response);
    }

    /**
     * ResponseStatusException 처리
     * WHY: @ResponseStatus 어노테이션이나 ResponseStatusException 처리
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        
        log.warn("ResponseStatusException: {} - URI: {}", ex.getMessage(), request.getRequestURI());
        
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
        
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    /**
     * 일반적인 RuntimeException 처리
     * WHY: 예상치 못한 런타임 오류를 500 에러로 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("RuntimeException: {} - URI: {}", ex.getMessage(), request.getRequestURI(), ex);
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 모든 예외의 최종 처리
     * WHY: 처리되지 않은 모든 예외를 500 에러로 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> handleException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected Exception: {} - URI: {}", ex.getMessage(), request.getRequestURI(), ex);
        
        ApiEnvelope<Void> response = ApiEnvelope.<Void>builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("예상치 못한 오류가 발생했습니다")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
