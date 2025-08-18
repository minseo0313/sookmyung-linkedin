package com.sookmyung.campus_match.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팀 캘린더 생성 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "팀 캘린더 생성 요청")
public class TeamCalendarRequest {

    @NotBlank(message = "이벤트 제목은 필수입니다")
    @Schema(description = "이벤트 제목", example = "팀 회의", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "이벤트 메모", example = "프로젝트 진행 상황 공유")
    private String notes;

    @NotNull(message = "시작 일시는 필수입니다")
    @Schema(description = "시작 일시", example = "2025-01-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startAt;

    @NotNull(message = "종료 일시는 필수입니다")
    @Schema(description = "종료 일시", example = "2025-01-15T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endAt;

    @Schema(description = "위치", example = "중앙도서관 회의실 A")
    private String location;
}
