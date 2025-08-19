package com.sookmyung.campus_match.controller.team;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.team.TeamResponse;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleRequest;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleResponse;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentRequest;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentResponse;
import com.sookmyung.campus_match.service.team.TeamService;
import com.sookmyung.campus_match.util.security.RequiresApproval;
import com.sookmyung.campus_match.util.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "팀", description = "팀 관리 관련 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "팀 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "승인되지 않은 사용자"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 팀")
    })
    @PostMapping
    @RequiresApproval(message = "승인된 사용자만 팀을 생성할 수 있습니다.")
    public ResponseEntity<ApiEnvelope<TeamResponse>> createTeam(
            @Parameter(description = "팀명", example = "프로젝트 A팀")
            @RequestParam String teamName,
            @Parameter(description = "팀 설명", example = "웹 개발 프로젝트를 진행하는 팀입니다.")
            @RequestParam String description,
            @Parameter(description = "최대 인원", example = "5")
            @RequestParam(required = false) Integer maxMembers,
            @Parameter(description = "게시글 ID", example = "1")
            @RequestParam(required = false) Long postId) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        TeamResponse team = teamService.createTeam(teamName, description, maxMembers, postId, currentUserId.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(team));
    }

    @Operation(summary = "팀 조회", description = "특정 팀의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 조회 성공"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<TeamResponse>> getTeam(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id) {
        
        TeamResponse team = teamService.getTeam(id);
        return ResponseEntity.ok(ApiEnvelope.success(team));
    }

    @Operation(summary = "팀 목록 조회", description = "팀 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 페이징 파라미터")
    })
    @GetMapping
    public ResponseEntity<ApiEnvelope<Page<TeamResponse>>> getTeams(
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=createdAt,desc")
            Pageable pageable) {
        
        Page<TeamResponse> teams = teamService.getTeams(pageable);
        return ResponseEntity.ok(ApiEnvelope.success(teams));
    }

    @Operation(summary = "내 팀 목록", description = "현재 사용자가 속한 팀 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 팀 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/my")
    public ResponseEntity<ApiEnvelope<List<TeamResponse>>> getMyTeams(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<TeamResponse> teams = teamService.getUserTeams(userDetails.getUsername());
        return ResponseEntity.ok(ApiEnvelope.success(teams));
    }

    @Operation(summary = "팀 멤버 추가", description = "팀에 새로운 멤버를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 멤버 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (팀장만 가능)"),
            @ApiResponse(responseCode = "404", description = "팀 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 팀 멤버이거나 팀 인원이 가득 참")
    })
    @PostMapping("/{id}/members")
    @RequiresApproval(message = "승인된 사용자만 팀 멤버를 추가할 수 있습니다.")
    public ResponseEntity<ApiEnvelope<String>> addMember(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "추가할 사용자 ID", example = "2")
            @RequestParam Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        teamService.addMember(id, userId, userDetails.getUsername());
        return ResponseEntity.ok(ApiEnvelope.success("팀 멤버가 추가되었습니다."));
    }

    @Operation(summary = "팀 멤버 제거", description = "팀에서 멤버를 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 멤버 제거 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (팀장만 가능)"),
            @ApiResponse(responseCode = "404", description = "팀 또는 사용자를 찾을 수 없음")
    })
    @DeleteMapping("/{id}/members/{userId}")
    @RequiresApproval(message = "승인된 사용자만 팀 멤버를 제거할 수 있습니다.")
    public ResponseEntity<ApiEnvelope<String>> removeMember(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "제거할 사용자 ID", example = "2")
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        teamService.removeMember(id, userId, userDetails.getUsername());
        return ResponseEntity.ok(ApiEnvelope.success("팀 멤버가 제거되었습니다."));
    }

    @Operation(summary = "팀 스케줄 생성", description = "팀에 새로운 스케줄을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "팀 스케줄 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (검증 실패)"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (팀장만 가능)"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @PostMapping("/{id}/schedules")
    @RequiresApproval(message = "승인된 사용자만 팀 스케줄을 생성할 수 있습니다.")
    public ResponseEntity<ApiEnvelope<TeamScheduleResponse>> createSchedule(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TeamScheduleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TeamScheduleResponse schedule = teamService.createSchedule(id, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(schedule));
    }

    @Operation(summary = "팀 스케줄 조회", description = "팀의 스케줄 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 스케줄 조회 성공"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @GetMapping("/{id}/schedules")
    public ResponseEntity<ApiEnvelope<List<TeamScheduleResponse>>> getTeamSchedules(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id) {
        
        List<TeamScheduleResponse> schedules = teamService.getTeamSchedules(id);
        return ResponseEntity.ok(ApiEnvelope.success(schedules));
    }

    @Operation(summary = "스케줄 할당", description = "스케줄에 팀원을 할당합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "스케줄 할당 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (검증 실패)"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "스케줄 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 할당된 스케줄")
    })
    @PostMapping("/schedules/{scheduleId}/assign")
    @RequiresApproval(message = "승인된 사용자만 스케줄을 할당할 수 있습니다.")
    public ResponseEntity<ApiEnvelope<ScheduleAssignmentResponse>> assignSchedule(
            @Parameter(description = "스케줄 ID", example = "1")
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleAssignmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ScheduleAssignmentResponse assignment = teamService.assignSchedule(scheduleId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(assignment));
    }

    @Operation(summary = "팀 비활성화", description = "팀을 비활성화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 비활성화 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (팀장만 가능)"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @PatchMapping("/{id}/deactivate")
    @RequiresApproval(message = "승인된 사용자만 팀을 비활성화할 수 있습니다.")
    public ResponseEntity<ApiEnvelope<String>> deactivateTeam(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        teamService.deactivateTeam(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiEnvelope.success("팀이 비활성화되었습니다."));
    }

    // Deprecated: 기존 오타 경로 지원 (302 리다이렉트)
    @GetMapping("/{id}/calender")
    public ResponseEntity<Void> getTeamCalendarRedirect(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/api/teams/" + id + "/calendar")
                .build();
    }
}
