package com.sookmyung.campus_match.dto.auth;

import lombok.*;

/**
 * 뱃지 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BadgeResponse {

    private Long userId;
    private String badgeType;
    private String badgeName;
    private String description;
    private Boolean isActive;

    public static BadgeResponse from(Long userId, com.sookmyung.campus_match.domain.common.enums.ApprovalStatus approvalStatus, boolean isOperator) {
        return BadgeResponse.builder()
                .userId(userId)
                .badgeType(approvalStatus.name())
                .badgeName(getBadgeName(approvalStatus))
                .description(getBadgeDescription(approvalStatus))
                .isActive(isOperator)
                .build();
    }

    private static String getBadgeName(com.sookmyung.campus_match.domain.common.enums.ApprovalStatus approvalStatus) {
        return switch (approvalStatus) {
            case APPROVED -> "승인된 사용자";
            case PENDING -> "승인 대기중";
            case REJECTED -> "승인 거부됨";
        };
    }

    private static String getBadgeDescription(com.sookmyung.campus_match.domain.common.enums.ApprovalStatus approvalStatus) {
        return switch (approvalStatus) {
            case APPROVED -> "모든 기능을 사용할 수 있습니다.";
            case PENDING -> "승인 후 모든 기능을 사용할 수 있습니다.";
            case REJECTED -> "승인이 거부되었습니다.";
        };
    }
}
