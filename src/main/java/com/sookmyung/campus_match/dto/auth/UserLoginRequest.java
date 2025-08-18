package com.sookmyung.campus_match.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 사용자 로그인 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserLoginRequest {

    @NotBlank(message = "학번은 필수입니다")
    private String studentId;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
