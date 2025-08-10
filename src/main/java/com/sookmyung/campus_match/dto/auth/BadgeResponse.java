package com.sookmyung.campus_match.dto.auth;

import com.sookmyung.campus_match.domain.user.enum_.ApprovalStatus;
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

    @Schema(description = "승인 상태", example = "APPROVED")
    private ApprovalStatus approvalStatus;

    @Schema(description = "배지 보유 여부", example = "true")
    private boolean hasBadge;

    public static BadgeResponse from(ApprovalStatus status) {
        return BadgeResponse.builder()
                .approvalStatus(status)
                .hasBadge(status == ApprovalStatus.APPROVED)
                .build();
    }
}
