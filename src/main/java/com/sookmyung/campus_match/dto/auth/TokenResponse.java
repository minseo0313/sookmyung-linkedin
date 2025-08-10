package com.sookmyung.campus_match.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * JWT 발급 응답 DTO.
 * - accessToken / refreshToken 모두 포함 (필요 시 refreshToken은 제외 가능)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String refreshToken;

    @Schema(description = "액세스 토큰 만료 시간(초)", example = "3600")
    private long expiresIn;
}
