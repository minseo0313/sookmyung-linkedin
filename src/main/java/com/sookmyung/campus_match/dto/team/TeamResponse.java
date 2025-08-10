package com.sookmyung.campus_match.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팀 정보 응답 DTO
 * - 팀 매칭 후 생성되는 팀 정보 반환
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamResponse {

    @Schema(description = "팀 ID", example = "1")
    private Long id;

    @Schema(description = "팀 이름", example = "캡스톤 디자인 팀")
    private String name;

    @Schema(description = "프로젝트 시작일", example = "2025-09-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "프로젝트 마감일", example = "2025-12-15T00:00:00")
    private LocalDateTime endDate;

    @Schema(description = "팀장 ID", example = "101")
    private Long leaderId;

    @Schema(description = "팀원 목록")
    private List<TeamMemberResponse> members;
}
