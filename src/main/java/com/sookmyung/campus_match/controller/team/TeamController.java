package com.sookmyung.campus_match.controller.team;

import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentRequest;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentResponse;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleRequest;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleResponse;
import com.sookmyung.campus_match.dto.team.TeamAcceptRequest;
import com.sookmyung.campus_match.dto.team.TeamMemberResponse;
import com.sookmyung.campus_match.dto.team.TeamResponse;
import com.sookmyung.campus_match.service.schedule.ScheduleService;
import com.sookmyung.campus_match.service.team.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팀", description = "팀 매칭 및 팀 비서 관련 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final ScheduleService scheduleService;

    @Operation(summary = "팀 매칭 확정", description = "신청 수락 및 팀 매칭을 확정합니다.")
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<TeamResponse>> acceptTeam(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TeamAcceptRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TeamResponse team = teamService.acceptTeam(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(team));
    }

    @Operation(summary = "팀 비서 캘린더 조회", description = "매칭된 팀의 일정을 조회합니다.")
    @GetMapping("/{id}/calendar")
    public ResponseEntity<ApiResponse<List<TeamScheduleResponse>>> getTeamCalendar(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<TeamScheduleResponse> schedules = scheduleService.getTeamSchedules(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }

    @Operation(summary = "일정 생성", description = "AI 스케줄링을 포함한 팀 일정을 생성합니다.")
    @PostMapping("/{id}/schedules")
    public ResponseEntity<ApiResponse<TeamScheduleResponse>> createSchedule(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TeamScheduleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TeamScheduleResponse schedule = scheduleService.createSchedule(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    @Operation(summary = "일정 수정", description = "팀 일정을 수정합니다. (팀장만 가능)")
    @PutMapping("/{id}/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<TeamScheduleResponse>> updateSchedule(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "일정 ID", example = "1")
            @PathVariable Long scheduleId,
            @Valid @RequestBody TeamScheduleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TeamScheduleResponse schedule = scheduleService.updateSchedule(id, scheduleId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    @Operation(summary = "개인 업무 할당 표 조회", description = "팀원별 업무를 확인합니다.")
    @GetMapping("/{id}/assignments")
    public ResponseEntity<ApiResponse<List<ScheduleAssignmentResponse>>> getAssignments(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<ScheduleAssignmentResponse> assignments = scheduleService.getTeamAssignments(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @Operation(summary = "일정 상세 조회", description = "특정 일정의 상세 정보를 조회합니다.")
    @GetMapping("/{teamId}/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<TeamScheduleResponse>> getScheduleDetail(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long teamId,
            @Parameter(description = "일정 ID", example = "1")
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TeamScheduleResponse schedule = scheduleService.getScheduleDetail(teamId, scheduleId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    @Operation(summary = "일정 삭제", description = "팀 일정을 삭제합니다. (팀장만 가능)")
    @DeleteMapping("/{teamId}/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<String>> deleteSchedule(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long teamId,
            @Parameter(description = "일정 ID", example = "1")
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        scheduleService.deleteSchedule(teamId, scheduleId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("일정이 삭제되었습니다."));
    }

    @Operation(summary = "업무 할당", description = "팀원에게 업무를 할당합니다.")
    @PostMapping("/{teamId}/schedules/{scheduleId}/assignments")
    public ResponseEntity<ApiResponse<ScheduleAssignmentResponse>> assignTask(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long teamId,
            @Parameter(description = "일정 ID", example = "1")
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleAssignmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ScheduleAssignmentResponse assignment = scheduleService.assignTask(teamId, scheduleId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(assignment));
    }

    @Operation(summary = "팀원 목록 조회", description = "팀의 멤버 목록을 조회합니다.")
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeamMembers(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<TeamMemberResponse> members = teamService.getTeamMembers(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(members));
    }
}
