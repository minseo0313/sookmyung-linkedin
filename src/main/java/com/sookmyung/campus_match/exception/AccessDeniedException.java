package com.sookmyung.campus_match.exception;

/**
 * 접근 권한이 없을 때 발생하는 예외
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String resourceName, Long resourceId) {
        super(String.format("Access denied to %s with id: %d", resourceName, resourceId));
    }
}




