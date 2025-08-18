package com.sookmyung.campus_match.controller.team;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.team.TeamMembershipResponse;
import com.sookmyung.campus_match.service.team.TeamMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 팀 멤버십 관련 컨트롤러
 */
@Tag(name = "Team Membership", description = "팀 멤버십 관련 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Validated
public class TeamMembershipController {

    private final TeamMembershipService teamMembershipService;

    @Operation(summary = "팀 초대 수락", description = "현재 사용자가 팀의 초대를 수락하여 멤버가 됩니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "팀 멤버십 생성 성공",
                    content = @Content(examples = {
                            @ExampleObject(name = "성공 응답",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "팀 멤버십이 성공적으로 생성되었습니다",
                                              "data": {
                                                "id": 1,
                                                "teamId": 1,
                                                "userId": 101,
                                                "role": "MEMBER",
                                                "createdAt": "2025-01-15T10:30:00"
                                              },
                                              "timestamp": "2025-01-15T10:30:00Z"
                                            }
                                            """)
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "팀 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 팀 멤버이거나 팀 인원이 가득 참"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiEnvelope<TeamMembershipResponse>> acceptInvite(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable("id") Long teamId,
            @Parameter(description = "현재 사용자 ID", example = "101")
            @RequestHeader("X-USER-ID") Long currentUserId) {

        TeamMembershipResponse response = teamMembershipService.acceptInvite(teamId, currentUserId);
        
        URI location = URI.create("/api/teams/" + teamId + "/members/" + currentUserId);
        
        return ResponseEntity.created(location)
                .body(ApiEnvelope.created(response));
    }
}
