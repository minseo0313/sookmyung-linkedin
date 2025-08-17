package com.sookmyung.campus_match.dto.team;

import com.sookmyung.campus_match.domain.team.Team;
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

    @Schema(description = "팀 설명", example = "웹 개발 프로젝트를 진행하는 팀입니다.")
    private String description;

    @Schema(description = "프로젝트 시작일", example = "2025-09-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "프로젝트 마감일", example = "2025-12-15T00:00:00")
    private LocalDateTime endDate;

    @Schema(description = "팀장 ID", example = "101")
    private Long leaderId;

    @Schema(description = "팀원 목록")
    private List<TeamMemberResponse> members;

    @Schema(description = "팀 활성화 상태", example = "true")
    private Boolean isActive;

    @Schema(description = "최대 인원", example = "5")
    private Integer maxMembers;

    @Schema(description = "현재 인원", example = "3")
    private Integer currentMemberCount;

    @Schema(description = "생성자 ID", example = "101")
    private Long createdById;

    @Schema(description = "생성 시간", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    public static TeamResponse from(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .startDate(null) // TODO: 실제 시작일 설정
                .endDate(null) // TODO: 실제 종료일 설정
                .leaderId(team.getCreatedBy() != null ? team.getCreatedBy().getId() : null)
                .members(List.of()) // TODO: 실제 팀원 목록을 가져와서 설정
                .isActive(team.isActive())
                .maxMembers(team.getMaxMembers())
                .currentMemberCount(team.getCurrentMemberCount())
                .createdById(team.getCreatedBy() != null ? team.getCreatedBy().getId() : null)
                .createdAt(team.getCreatedAt())
                .build();
    }
}
