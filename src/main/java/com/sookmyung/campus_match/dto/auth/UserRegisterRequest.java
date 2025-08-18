package com.sookmyung.campus_match.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 사용자 등록 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRegisterRequest {

    @NotBlank(message = "학번은 필수입니다")
    @Pattern(regexp = "^[0-9]{8,10}$", message = "학번은 8-10자리 숫자여야 합니다")
    private String studentId;

    @NotBlank(message = "학과는 필수입니다")
    @Size(max = 255, message = "학과명은 255자를 초과할 수 없습니다")
    private String department;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 255, message = "이름은 255자를 초과할 수 없습니다")
    private String name;

    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birthDate;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$", message = "전화번호 형식이 올바르지 않습니다")
    private String phoneNumber;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8-100자여야 합니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$", 
             message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String password;
}
