package com.sookmyung.campus_match.config.security.dev;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class DevPrincipalResolver {

    private static final String MOCK_USER_HEADER = "X-Mock-User";
    private static final String DEFAULT_USERNAME = "devuser";

    /**
     * dev 환경에서 현재 사용자의 username을 안전하게 가져옵니다.
     * WHY: dev 환경에서는 principal이 없을 수 있으므로 헤더 → SecurityContext → 기본값 순으로 확인
     */
    public String currentUsername(HttpServletRequest request) {
        // 1. X-Mock-User 헤더 우선 확인
        String mockUser = request.getHeader(MOCK_USER_HEADER);
        if (mockUser != null && !mockUser.trim().isEmpty()) {
            log.debug("Dev 환경: X-Mock-User 헤더에서 username 획득 - {}", mockUser);
            return mockUser.trim();
        }

        // 2. SecurityContext에서 Authentication 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null && !authentication.getName().equals("anonymousUser")) {
            log.debug("Dev 환경: SecurityContext에서 username 획득 - {}", authentication.getName());
            return authentication.getName();
        }

        // 3. 기본값 사용
        log.debug("Dev 환경: 기본 username 사용 - {}", DEFAULT_USERNAME);
        return DEFAULT_USERNAME;
    }

    /**
     * dev 환경에서 현재 사용자의 ID를 안전하게 가져옵니다.
     * WHY: username을 ID로 변환할 때 안전하게 처리하기 위함
     */
    public Long currentUserId(HttpServletRequest request) {
        String username = currentUsername(request);
        try {
            // dev 환경에서는 username이 숫자 ID 형태일 것으로 가정
            return Long.valueOf(username);
        } catch (NumberFormatException e) {
            log.warn("Dev 환경: username을 ID로 변환 실패 - {}, 기본값 1 사용", username);
            return 1L; // 기본 사용자 ID
        }
    }
}

