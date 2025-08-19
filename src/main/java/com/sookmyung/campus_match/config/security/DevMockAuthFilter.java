package com.sookmyung.campus_match.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@Profile("dev")
public class DevMockAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // X-Mock-User 헤더가 있으면 해당 값을 사용, 없으면 기본값 사용
        String mockUser = request.getHeader("X-Mock-User");
        if (mockUser == null || mockUser.trim().isEmpty()) {
            mockUser = "devuser";
        }
        
        log.debug("Dev 환경 가짜 인증 적용 - 사용자: {}, 요청 URI: {}", mockUser, request.getRequestURI());
        
        // 가짜 인증 토큰 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            mockUser,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        filterChain.doFilter(request, response);
    }
}
