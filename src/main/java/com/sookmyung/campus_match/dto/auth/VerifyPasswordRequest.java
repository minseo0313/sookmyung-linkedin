package com.sookmyung.campus_match.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "비밀번호 확인 요청")
public class VerifyPasswordRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다")
    @Schema(description = "현재 비밀번호", example = "password123!")
    private String currentPassword;
}
