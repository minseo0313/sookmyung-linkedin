package com.sookmyung.campus_match.dto.auth;

import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String studentId;
    private String department;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private ApprovalStatus approvalStatus;
    private Boolean isDeleted;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
