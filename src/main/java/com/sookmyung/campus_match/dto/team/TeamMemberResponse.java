package com.sookmyung.campus_match.dto.team;

import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 팀원 정보 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamMemberResponse {

    @Schema(description = "팀원 ID", example = "101")
    private Long userId;

    @Schema(description = "팀원 이름", example = "홍길동")
    private String fullName;

    @Schema(description = "팀원 학과", example = "컴퓨터공학과")
    private String department;

    @Schema(description = "팀원 역할", example = "MEMBER")
    private MemberRole role;

    public static TeamMemberResponse from(TeamMember member) {
        return TeamMemberResponse.builder()
                .userId(member.getUser() != null ? member.getUser().getId() : null)
                .fullName(member.getUser() != null ? member.getUser().getFullName() : null)
                .department(member.getUser() != null ? member.getUser().getDepartment() : null)
                .role(member.getRole())
                .build();
    }
}
