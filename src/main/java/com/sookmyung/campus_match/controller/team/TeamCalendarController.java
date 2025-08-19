package com.sookmyung.campus_match.controller.team;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.team.TeamCalendarRequest;
import com.sookmyung.campus_match.dto.team.TeamCalendarResponse;
import com.sookmyung.campus_match.dto.team.TeamEventResponse;
import com.sookmyung.campus_match.service.team.TeamCalendarService;
import com.sookmyung.campus_match.util.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 팀 캘린더 관련 컨트롤러
 */
@Tag(name = "Team Calendar", description = "팀 캘린더 관련 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Validated
public class TeamCalendarController {

    private final TeamCalendarService teamCalendarService;

    @Operation(summary = "팀 캘린더 조회", description = "팀의 캘린더 이벤트 목록을 조회합니다. 팀 멤버만 접근 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 캘린더 조회 성공",
                    content = @Content(examples = {
                            @ExampleObject(name = "성공 응답",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "OK",
                                              "message": "팀 캘린더 조회 성공",
                                              "data": {
                                                "teamId": 1,
                                                "events": [
                                                  {
                                                    "id": 1,
                                                    "title": "팀 회의",
                                                    "startAt": "2025-01-15T10:00:00",
                                                    "endAt": "2025-01-15T12:00:00",
                                                    "location": "중앙도서관 회의실 A",
                                                    "notes": "프로젝트 진행 상황 공유"
                                                  },
                                                  {
                                                    "id": 2,
                                                    "title": "코드 리뷰",
                                                    "startAt": "2025-01-16T14:00:00",
                                                    "endAt": "2025-01-16T16:00:00",
                                                    "location": "온라인",
                                                    "notes": "백엔드 API 코드 리뷰"
                                                  }
                                                ]
                                              },
                                              "timestamp": "2025-01-15T10:30:00Z"
                                            }
                                            """)
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "팀 멤버가 아님"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}/calendar")
    public ResponseEntity<ApiEnvelope<TeamCalendarResponse>> getTeamCalendar(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable("id") Long teamId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        TeamCalendarResponse response = teamCalendarService.getTeamCalendar(teamId, currentUserId);
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    @Operation(summary = "팀 캘린더 이벤트 생성", description = "팀 캘린더에 새로운 이벤트를 생성합니다. 팀 멤버만 접근 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "팀 캘린더 이벤트 생성 성공",
                    content = @Content(examples = {
                            @ExampleObject(name = "성공 응답",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "팀 캘린더 이벤트가 성공적으로 생성되었습니다",
                                              "data": {
                                                "id": 3,
                                                "title": "팀 회의",
                                                "startAt": "2025-01-20T10:00:00",
                                                "endAt": "2025-01-20T12:00:00",
                                                "location": "중앙도서관 회의실 A",
                                                "notes": "프로젝트 진행 상황 공유"
                                              },
                                              "timestamp": "2025-01-15T10:30:00Z"
                                            }
                                            """)
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (시작 시간이 종료 시간보다 늦음)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "팀 멤버가 아님"),
            @ApiResponse(responseCode = "404", description = "팀 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "중복된 이벤트"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/calendar")
    public ResponseEntity<ApiEnvelope<TeamEventResponse>> createTeamCalendarEvent(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable("id") Long teamId,
            @Parameter(description = "캘린더 이벤트 생성 요청")
            @Valid @RequestBody TeamCalendarRequest request) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        TeamEventResponse response = teamCalendarService.createTeamCalendarEvent(teamId, request, currentUserId);
        
        URI location = URI.create("/api/teams/" + teamId + "/calendar/events/" + response.getId());
        
        return ResponseEntity.created(location)
                .body(ApiEnvelope.created(response));
    }
}
