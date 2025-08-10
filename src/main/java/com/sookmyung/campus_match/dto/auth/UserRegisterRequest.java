package com.sookmyung.campus_match.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 회원 가입 요청 DTO.
 * - 규칙: username=학번, sookmyungEmail만 사용
 * - 비밀번호 규칙(대/소문자+숫자+특수문자 포함)은 DTO가 아닌 Validator/Service에서 검증
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRegisterRequest {

    @Schema(description = "이름(실명)", example = "남민서")
    @NotBlank
    @Size(max = 60)
    private String fullName;

    @Schema(description = "생년월일(ISO-8601)", example = "2001-05-21")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private java.time.LocalDate birthDate;

    @Schema(description = "전화번호(하이픈 가능)", example = "010-1234-5678")
    @NotBlank
    @Size(max = 30)
    private String phone;

    @Schema(description = "학번(별도 보관)", example = "20251234")
    @NotBlank
    @Size(max = 50)
    private String studentId;

    @Schema(description = "학과", example = "소프트웨어융합전공")
    @NotBlank
    @Size(max = 80)
    private String department;

    @Schema(description = "숙명 이메일", example = "minseo.nam@sookmyung.ac.kr")
    @NotBlank
    @Email
    @Size(max = 120)
    private String sookmyungEmail;

    @Schema(description = "로그인 아이디(=학번)", example = "20251234")
    @NotBlank
    @Size(max = 50)
    private String username;

    @Schema(description = "비밀번호(6~10자) — 조합 규칙은 서비스에서 검증", example = "Abc123!")
    @NotBlank
    @Size(min = 6, max = 10)
    private String password;
}
