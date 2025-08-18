package com.sookmyung.campus_match.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 팀 캘린더 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "팀 캘린더 응답")
public class TeamCalendarResponse {

    @Schema(description = "팀 ID", example = "1")
    private Long teamId;

    @Schema(description = "이벤트 목록")
    private List<TeamEventResponse> events;

    public static TeamCalendarResponse of(Long teamId, List<TeamEventResponse> events) {
        return TeamCalendarResponse.builder()
                .teamId(teamId)
                .events(events)
                .build();
    }
}
