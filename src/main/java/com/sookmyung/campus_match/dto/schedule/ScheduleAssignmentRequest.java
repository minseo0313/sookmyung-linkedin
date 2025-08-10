package com.sookmyung.campus_match.dto.schedule;

import com.sookmyung.campus_match.domain.team.enum_.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 일정 업무 할당 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScheduleAssignmentRequest {

    @Schema(description = "할당받는 사용자 ID", example = "5")
    private Long assigneeId;

    @Schema(description = "업무 제목", example = "디자인 시안 제작")
    private String title;

    @Schema(description = "업무 설명", example = "메인 페이지 UI/UX 시안 제작")
    private String description;

    @Schema(description = "업무 상태", example = "TODO")
    private AssignmentStatus status;

    @Schema(description = "마감 기한", example = "2025-08-20T18:00:00")
    private LocalDateTime dueAt;

    @Schema(description = "진행률(%)", example = "0")
    private Integer progressPct;
}
