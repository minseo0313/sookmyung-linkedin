package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Dev 환경용 인증 스텁 컨트롤러
 * 실제 인증 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Profile("dev")
public class DevAuthController {

    private final CurrentUserResolver currentUserResolver;

    /**
     * Dev 환경용 로그인 스텁
     */
    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<LoginResponse>> login(@RequestBody LoginRequest request) {
        log.info("Dev 환경 - 로그인 스텁 호출: {}", request.username);
        
        LoginResponse response = LoginResponse.builder()
                .accessToken("dev-access-token-" + System.currentTimeMillis())
                .refreshToken("dev-refresh-token-" + System.currentTimeMillis())
                .userId(currentUserResolver.currentUserId())
                .build();
        
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    /**
     * Dev 환경용 회원가입 스텁
     */
    @PostMapping("/register")
    public ResponseEntity<ApiEnvelope<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        log.info("Dev 환경 - 회원가입 스텁 호출: {}", request.username);
        
        Long userId = currentUserResolver.currentUserId();
        
        RegisterResponse response = RegisterResponse.builder()
                .id(userId)
                .email(request.email)
                .name(request.username)
                .accessToken("dev-access-token-" + System.currentTimeMillis())
                .refreshToken("dev-refresh-token-" + System.currentTimeMillis())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + userId)
                .body(ApiEnvelope.created(response));
    }

    // Request DTOs
    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
    }

    // Response DTOs
    @lombok.Builder
    public static class LoginResponse {
        public String accessToken;
        public String refreshToken;
        public Long userId;
    }

    @lombok.Builder
    public static class RegisterResponse {
        public Long id;
        public String email;
        public String name;
        public String accessToken;
        public String refreshToken;
    }
}

