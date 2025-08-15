package com.sookmyung.campus_match.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

/**
 * 사용자 기본 정보 응답 DTO.
 * - 민감정보는 포함하지 않음(비밀번호, 신고수, operator 등 제외)
 * - 주로 관리자 조회, 프로필 표시 등에서 사용
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "로그인 아이디(=학번)", example = "20251234")
    private String username;

    @Schema(description = "학생 ID(별도 보관)", example = "20251234")
    private String studentId;

    @Schema(description = "숙명 이메일", example = "minseo.nam@sookmyung.ac.kr")
    private String sookmyungEmail;

    @Schema(description = "이름(실명)", example = "남민서")
    private String fullName;

    @Schema(description = "생년월일(ISO-8601)", example = "2001-05-21")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "학과", example = "소프트웨어융합전공")
    private String department;

    @Schema(description = "승인 상태", example = "APPROVED")
    private ApprovalStatus approvalStatus;

    // Factory method
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .studentId(user.getStudentId())
                .sookmyungEmail(user.getSookmyungEmail())
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .approvalStatus(user.getApprovalStatus())
                .build();
    }
}
