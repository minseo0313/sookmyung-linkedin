package com.sookmyung.campus_match.dto.team;

import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팀 멤버십 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "팀 멤버십 응답")
public class TeamMembershipResponse {

    @Schema(description = "팀 멤버십 ID", example = "1")
    private Long id;

    @Schema(description = "팀 ID", example = "1")
    private Long teamId;

    @Schema(description = "사용자 ID", example = "101")
    private Long userId;

    @Schema(description = "멤버 역할", example = "MEMBER")
    private MemberRole role;

    @Schema(description = "가입 시각", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    public static TeamMembershipResponse from(TeamMember teamMember) {
        return TeamMembershipResponse.builder()
                .id(teamMember.getId())
                .teamId(teamMember.getTeam().getId())
                .userId(teamMember.getUser().getId())
                .role(teamMember.getRole())
                .createdAt(teamMember.getCreatedAt())
                .build();
    }
}
