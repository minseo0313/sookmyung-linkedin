package com.sookmyung.campus_match.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팀 스케줄 수정 요청 DTO
 */
@Schema(description = "팀 스케줄 수정 요청. 부분 업데이트를 허용하되, startAt/endAt는 함께 제공되어야 하며, 함께 올 경우 startAt < endAt 이어야 합니다.")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamScheduleUpdateRequest {

    @Schema(description = "제목", example = "주간 스프린트 회의", maxLength = 255)
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Schema(description = "설명", example = "스프린트 진행상황 공유", maxLength = 1000)
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    private String description;

    @Schema(description = "시작 시각", example = "2025-02-01T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @Schema(description = "종료 시각", example = "2025-02-01T11:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    @Schema(description = "장소", example = "중앙도서관 회의실 B", maxLength = 255)
    @Size(max = 255, message = "장소는 255자를 초과할 수 없습니다")
    private String location;

    @AssertTrue(message = "startAt/endAt는 함께 제공되어야 하며, 함께 올 경우 startAt는 endAt보다 이전이어야 합니다")
    @Schema(hidden = true)
    public boolean isValidDateRange() {
        if (startAt == null && endAt == null) {
            return true;
        }
        if (startAt == null || endAt == null) {
            return false;
        }
        return startAt.isBefore(endAt);
    }
}
