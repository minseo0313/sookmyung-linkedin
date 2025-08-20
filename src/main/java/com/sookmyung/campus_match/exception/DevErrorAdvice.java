package com.sookmyung.campus_match.exception;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Dev 환경에서 예외를 200 OK로 감싸서 반환하는 Advice
 */
@Slf4j
@RestControllerAdvice
@Profile("dev")
public class DevErrorAdvice {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        
        log.warn("Dev 환경 - MethodArgumentTypeMismatchException: {} at {}", 
                ex.getMessage(), ex.getParameter().getParameterName());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("reason", "파라미터 타입 불일치");
        errorDetails.put("parameter", ex.getParameter().getParameterName());
        errorDetails.put("value", ex.getValue());
        errorDetails.put("expectedType", ex.getParameter().getParameterType().getSimpleName());
        errorDetails.put("path", ex.getParameter().getMethod().getDeclaringClass().getSimpleName() + "." + ex.getParameter().getMethod().getName());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("debugMessage", ex.getMessage());
        
        return ResponseEntity.ok()
                .header("X-Dev-Relaxed-Error", "true")
                .body(ApiEnvelope.<Map<String, Object>>builder()
                        .success(true)
                        .code("DEV_RELAXED_ERROR")
                        .message("Dev 환경에서 안전하게 처리된 타입 불일치 오류")
                        .data(errorDetails)
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        
        log.warn("Dev 환경 - MethodArgumentNotValidException: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("reason", "검증 실패");
        errorDetails.put("fieldErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "rejectedValue", error.getRejectedValue(),
                        "message", error.getDefaultMessage()
                )).toList());
        errorDetails.put("path", ex.getParameter().getMethod().getDeclaringClass().getSimpleName() + "." + ex.getParameter().getMethod().getName());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("debugMessage", ex.getMessage());
        
        return ResponseEntity.ok()
                .header("X-Dev-Relaxed-Error", "true")
                .body(ApiEnvelope.<Map<String, Object>>builder()
                        .success(true)
                        .code("DEV_RELAXED_ERROR")
                        .message("Dev 환경에서 안전하게 처리된 검증 오류")
                        .data(errorDetails)
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        
        log.warn("Dev 환경 - HttpMessageNotReadableException: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("reason", "잘못된 JSON 형식");
        errorDetails.put("path", "JSON 파싱");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("debugMessage", ex.getMessage());
        
        // JSON 파싱 오류는 dev 환경에서도 400으로 처리
        return ResponseEntity.badRequest()
                .header("X-Dev-Relaxed-Error", "true")
                .body(ApiEnvelope.<Map<String, Object>>builder()
                        .success(false)
                        .code("BAD_REQUEST")
                        .message("잘못된 JSON 형식입니다")
                        .data(errorDetails)
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {
        
        log.warn("Dev 환경 - MissingServletRequestParameterException: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("reason", "필수 파라미터 누락");
        errorDetails.put("parameterName", ex.getParameterName());
        errorDetails.put("parameterType", ex.getParameterType());
        errorDetails.put("path", "Request Parameter");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("debugMessage", ex.getMessage());
        
        return ResponseEntity.ok()
                .header("X-Dev-Relaxed-Error", "true")
                .body(ApiEnvelope.<Map<String, Object>>builder()
                        .success(true)
                        .code("DEV_RELAXED_ERROR")
                        .message("Dev 환경에서 안전하게 처리된 파라미터 누락 오류")
                        .data(errorDetails)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> handleConstraintViolation(
            ConstraintViolationException ex) {
        
        log.warn("Dev 환경 - ConstraintViolationException: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("reason", "제약 조건 위반");
        errorDetails.put("violations", ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "propertyPath", violation.getPropertyPath().toString(),
                        "invalidValue", violation.getInvalidValue(),
                        "message", violation.getMessage()
                )).toList());
        errorDetails.put("path", "Validation");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("debugMessage", ex.getMessage());
        
        return ResponseEntity.ok()
                .header("X-Dev-Relaxed-Error", "true")
                .body(ApiEnvelope.<Map<String, Object>>builder()
                        .success(true)
                        .code("DEV_RELAXED_ERROR")
                        .message("Dev 환경에서 안전하게 처리된 제약 조건 위반 오류")
                        .data(errorDetails)
                        .build());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiEnvelope<Map<String, Object>>> handleBindException(
            BindException ex) {
        
        log.warn("Dev 환경 - BindException: {}", ex.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("reason", "바인딩 실패");
        errorDetails.put("fieldErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "rejectedValue", error.getRejectedValue(),
                        "message", error.getDefaultMessage()
                )).toList());
        errorDetails.put("path", "Data Binding");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("debugMessage", ex.getMessage());
        
        return ResponseEntity.ok()
                .header("X-Dev-Relaxed-Error", "true")
                .body(ApiEnvelope.<Map<String, Object>>builder()
                        .success(true)
                        .code("DEV_RELAXED_ERROR")
                        .message("Dev 환경에서 안전하게 처리된 바인딩 오류")
                        .data(errorDetails)
                        .build());
    }
}

