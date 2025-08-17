package com.sookmyung.campus_match.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청")
public class UserRegisterRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    @Schema(description = "생년월일", example = "2000-01-01")
    private LocalDate birthDate;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호 형식이 올바르지 않습니다")
    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @NotBlank(message = "학번은 필수입니다")
    @Size(min = 8, max = 20, message = "학번은 8자 이상 20자 이하여야 합니다")
    @Schema(description = "학번", example = "20240001")
    private String studentId;

    @NotBlank(message = "학과는 필수입니다")
    @Size(max = 100, message = "학과명은 100자 이하여야 합니다")
    @Schema(description = "학과", example = "컴퓨터학부")
    private String department;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Schema(description = "이메일", example = "student@sookmyung.ac.kr")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$", 
             message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    @Schema(description = "비밀번호", example = "password123!")
    private String password;
}
