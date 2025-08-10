package com.sookmyung.campus_match.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 로그인 요청 DTO.
 * - username = 학번
 * - password는 평문으로 받고, 서비스에서 해시 비교
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserLoginRequest {

    @Schema(description = "로그인 아이디(=학번)", example = "20251234")
    @NotBlank
    @Size(max = 50)
    private String username;

    @Schema(description = "비밀번호", example = "Abc123!")
    @NotBlank
    @Size(min = 6, max = 10)
    private String password;
}
