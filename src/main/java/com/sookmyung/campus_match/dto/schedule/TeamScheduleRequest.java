package com.sookmyung.campus_match.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팀 일정 생성/수정 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamScheduleRequest {

    @Schema(description = "일정 제목", example = "팀 회의", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "일정 설명", example = "프로젝트 진행 상황 공유")
    private String description;

    @Schema(description = "시작 일시", example = "2025-08-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startAt;

    @Schema(description = "종료 일시", example = "2025-08-15T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endAt;

    @Schema(description = "하루 종일 여부", example = "false")
    private boolean allDay;

    @Schema(description = "위치", example = "중앙도서관 회의실 A")
    private String location;
}
