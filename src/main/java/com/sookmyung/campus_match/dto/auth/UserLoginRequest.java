package com.sookmyung.campus_match.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "로그인 요청")
public class UserLoginRequest {

    @NotBlank(message = "학번은 필수입니다")
    @Schema(description = "학번", example = "20240001")
    private String studentId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "password123!")
    private String password;
}
