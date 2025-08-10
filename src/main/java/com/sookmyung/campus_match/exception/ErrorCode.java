package com.sookmyung.campus_match.exception;

/**
 * API 에러 코드 정의
 */
public enum ErrorCode {
    // 인증/인가 관련
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다."),
    
    // 사용자 관련
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    
    // 게시글 관련
    POST_NOT_FOUND("POST_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED("POST_ACCESS_DENIED", "게시글에 접근할 권한이 없습니다."),
    
    // 댓글 관련
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED("COMMENT_ACCESS_DENIED", "댓글에 접근할 권한이 없습니다."),
    
    // 메시지 관련
    MESSAGE_NOT_FOUND("MESSAGE_NOT_FOUND", "메시지를 찾을 수 없습니다."),
    MESSAGE_ACCESS_DENIED("MESSAGE_ACCESS_DENIED", "메시지에 접근할 권한이 없습니다."),
    
    // 팀 관련
    TEAM_NOT_FOUND("TEAM_NOT_FOUND", "팀을 찾을 수 없습니다."),
    TEAM_ACCESS_DENIED("TEAM_ACCESS_DENIED", "팀에 접근할 권한이 없습니다."),
    
    // 검증 관련
    VALIDATION_ERROR("VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
    
    // 서버 관련
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "서비스를 사용할 수 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
