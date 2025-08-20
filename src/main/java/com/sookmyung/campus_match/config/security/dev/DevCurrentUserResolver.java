package com.sookmyung.campus_match.config.security.dev;

import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Dev 환경용 현재 사용자 ID 해결기
 * 고정된 사용자 ID(1L)를 반환합니다.
 */
@Component
@Profile("dev")
public class DevCurrentUserResolver implements CurrentUserResolver {
    
    @Override
    public Long currentUserId() {
        return 1L;
    }
}

