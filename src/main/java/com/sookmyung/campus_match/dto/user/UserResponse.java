package com.sookmyung.campus_match.dto.user;

import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 응답")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "학번", example = "20240001")
    private String studentId;

    @Schema(description = "학과", example = "컴퓨터학부")
    private String department;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "생년월일", example = "2000-01-01")
    private LocalDate birthDate;

    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "이메일", example = "student@sookmyung.ac.kr")
    private String email;

    @Schema(description = "승인 상태", example = "APPROVED")
    private ApprovalStatus approvalStatus;

    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    @Schema(description = "마지막 로그인 시간", example = "2025-08-15T10:30:00")
    private LocalDateTime lastLoginAt;

    @Schema(description = "생성 시간", example = "2025-08-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2025-08-15T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "신고 누적 횟수", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer reportCount;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .department(user.getDepartment())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .approvalStatus(user.getApprovalStatus())
                .isDeleted(user.getIsDeleted())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .reportCount(user.getReportCount())
                .build();
    }
}
