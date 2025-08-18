package com.sookmyung.campus_match.dto.team;

import com.sookmyung.campus_match.domain.team.TeamSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팀 이벤트 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "팀 이벤트 응답")
public class TeamEventResponse {

    @Schema(description = "이벤트 ID", example = "1")
    private Long id;

    @Schema(description = "이벤트 제목", example = "팀 회의")
    private String title;

    @Schema(description = "시작 일시", example = "2025-01-15T10:00:00")
    private LocalDateTime startAt;

    @Schema(description = "종료 일시", example = "2025-01-15T12:00:00")
    private LocalDateTime endAt;

    @Schema(description = "위치", example = "중앙도서관 회의실 A")
    private String location;

    @Schema(description = "메모", example = "프로젝트 진행 상황 공유")
    private String notes;

    public static TeamEventResponse from(TeamSchedule schedule) {
        return TeamEventResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startAt(schedule.getStartAt())
                .endAt(schedule.getEndAt())
                .location(schedule.getLocation())
                .notes(schedule.getDescription())
                .build();
    }
}
