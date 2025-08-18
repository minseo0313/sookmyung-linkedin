package com.sookmyung.campus_match.dto.schedule;

import com.sookmyung.campus_match.domain.team.TeamSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팀 일정 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamScheduleResponse {

    @Schema(description = "일정 ID", example = "10")
    private Long id;

    @Schema(description = "일정 제목", example = "팀 회의")
    private String title;

    @Schema(description = "일정 설명", example = "프로젝트 진행 상황 공유")
    private String description;

    @Schema(description = "시작 일시", example = "2025-08-15T10:00:00")
    private LocalDateTime startAt;

    @Schema(description = "종료 일시", example = "2025-08-15T12:00:00")
    private LocalDateTime endAt;

    @Schema(description = "하루 종일 여부", example = "false")
    private boolean allDay;

    @Schema(description = "위치", example = "중앙도서관 회의실 A")
    private String location;

    @Schema(description = "업무 목록")
    private List<ScheduleAssignmentResponse> assignments;

    public static TeamScheduleResponse from(TeamSchedule schedule) {
        return TeamScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .startAt(schedule.getStartAt())
                .endAt(schedule.getEndAt())
                .allDay(false) // 기본값으로 설정
                .location(schedule.getLocation())
                .assignments(List.of()) // TODO: 실제 assignments 목록 설정
                .build();
    }
}
