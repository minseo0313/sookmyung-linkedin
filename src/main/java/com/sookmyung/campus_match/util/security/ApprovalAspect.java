package com.sookmyung.campus_match.util.security;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApprovalAspect {

    private final UserRepository userRepository;

    /**
     * @RequiresApproval 어노테이션이 붙은 메서드 실행 전 권한 체크
     */
    @Before("@annotation(requiresApproval)")
    public void checkApproval(JoinPoint joinPoint, RequiresApproval requiresApproval) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 승인되지 않은 사용자(PENDING)는 접근 불가
        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            log.warn("승인되지 않은 사용자 {}가 승인 필요 기능에 접근 시도: {}", 
                    username, joinPoint.getSignature().getName());
            throw new ApiException(ErrorCode.FORBIDDEN, requiresApproval.message());
        }
    }
}
