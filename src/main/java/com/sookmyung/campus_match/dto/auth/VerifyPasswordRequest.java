package com.sookmyung.campus_match.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 비밀번호 검증 요청 DTO.
 * - 비밀번호 변경 전, 현재 비밀번호가 맞는지 확인하는 용도
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VerifyPasswordRequest {

    @Schema(description = "현재 비밀번호", example = "Abc123!")
    @NotBlank
    @Size(min = 6, max = 10)
    private String currentPassword;
}
