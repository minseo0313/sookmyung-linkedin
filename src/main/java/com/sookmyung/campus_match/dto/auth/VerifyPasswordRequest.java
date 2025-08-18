package com.sookmyung.campus_match.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 비밀번호 확인 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VerifyPasswordRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private String currentPassword;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    public String getCurrentPassword() {
        return currentPassword;
    }
}
