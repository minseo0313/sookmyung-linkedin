package com.sookmyung.campus_match.exception;

/**
 * 인증 실패 예외
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String reason) {
        super(reason);
    }
}
