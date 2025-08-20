package com.sookmyung.campus_match.config.security;

/**
 * 현재 사용자 ID를 해결하는 인터페이스
 */
public interface CurrentUserResolver {
    
    /**
     * 현재 사용자의 ID를 반환합니다.
     * 
     * @return 현재 사용자 ID
     */
    Long currentUserId();
}

