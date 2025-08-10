package com.sookmyung.campus_match.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 팀 매칭 수락 요청 DTO
 * - 게시글 신청 수락 시 매칭을 확정하기 위해 사용
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamAcceptRequest {

    @Schema(description = "수락할 게시글 신청자 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long applicantId;
}
