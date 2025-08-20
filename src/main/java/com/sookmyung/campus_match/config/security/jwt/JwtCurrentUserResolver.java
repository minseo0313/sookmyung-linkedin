package com.sookmyung.campus_match.config.security.jwt;

import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Prod 환경용 현재 사용자 ID 해결기
 * JWT 토큰에서 사용자 ID를 추출합니다.
 */
@Component
@Profile("prod")
public class JwtCurrentUserResolver implements CurrentUserResolver {
    
    @Override
    public Long currentUserId() {
        // TODO: SecurityContext에서 userId를 꺼내는 로직 구현
        // SecurityContextHolder.getContext().getAuthentication()에서 사용자 ID 추출
        throw new UnsupportedOperationException("JWT 기반 사용자 ID 해결 로직이 아직 구현되지 않았습니다.");
    }
}

