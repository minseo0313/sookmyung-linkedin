package com.sookmyung.campus_match.dto.auth;

import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 배지(승인 여부) 응답 DTO.
 * - 승인 여부와 상태를 간단하게 반환
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BadgeResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "승인 상태", example = "APPROVED")
    private ApprovalStatus approvalStatus;

    @Schema(description = "배지 보유 여부", example = "true")
    private boolean hasBadge;

    @Schema(description = "운영진 여부", example = "false")
    private boolean isOperator;

    public static BadgeResponse from(ApprovalStatus status) {
        return BadgeResponse.builder()
                .approvalStatus(status)
                .hasBadge(status == ApprovalStatus.APPROVED)
                .build();
    }

    public static BadgeResponse from(Long userId, ApprovalStatus status, boolean isOperator) {
        return BadgeResponse.builder()
                .userId(userId)
                .approvalStatus(status)
                .hasBadge(status == ApprovalStatus.APPROVED)
                .isOperator(isOperator)
                .build();
    }
}
