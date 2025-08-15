package com.sookmyung.campus_match.dto.schedule;

import com.sookmyung.campus_match.domain.team.ScheduleAssignment;
import com.sookmyung.campus_match.domain.common.enums.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 일정 업무 할당 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScheduleAssignmentResponse {

    @Schema(description = "업무 할당 ID", example = "15")
    private Long id;

    @Schema(description = "할당받는 사용자 ID", example = "5")
    private Long assigneeId;

    @Schema(description = "할당받는 사용자 이름", example = "홍길동")
    private String assigneeName;

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

    @Schema(description = "생성일시", example = "2025-08-10T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-08-11T09:15:00")
    private LocalDateTime updatedAt;

    public static ScheduleAssignmentResponse from(ScheduleAssignment assignment) {
        return ScheduleAssignmentResponse.builder()
                .id(assignment.getId())
                .assigneeId(assignment.getAssignee() != null ? assignment.getAssignee().getId() : null)
                .assigneeName(assignment.getAssignee() != null ? assignment.getAssignee().getFullName() : null)
                .title(assignment.getTitle())
                .description("") // TODO: 실제 description 값 설정
                .status(assignment.getStatus())
                .dueAt(null) // TODO: 실제 dueAt 값 설정
                .progressPct(0) // TODO: 실제 progressPct 값 설정
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}
