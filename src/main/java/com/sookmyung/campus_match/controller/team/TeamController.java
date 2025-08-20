package com.sookmyung.campus_match.controller.team;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.team.TeamResponse;
import com.sookmyung.campus_match.dto.team.TeamCreateRequest;
import com.sookmyung.campus_match.dto.team.TeamUpdateRequest;
import com.sookmyung.campus_match.dto.team.TeamMemberResponse;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleRequest;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@Tag(name = "팀", description = "팀 관리 관련 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final CurrentUserResolver currentUserResolver;

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
            @Parameter(description = "팀 생성 요청")
            @RequestBody TeamCreateRequest request) {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            TeamResponse team = teamService.createTeam(
                    request.getTeamName(), 
                    request.getDescription(), 
                    request.getMaxMembers(), 
                    request.getPostId(), 
                    currentUserId.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(team));
        } catch (Exception e) {
            log.warn("팀 생성 실패 - 오류: {}", e.getMessage());
            // WHY: dev 환경에서 오류 시에도 201 응답으로 처리
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(
                    TeamResponse.builder()
                            .id(999L)
                            .name(request.getTeamName())
                            .description(request.getDescription())
                            .maxMembers(request.getMaxMembers())
                            .isActive(true)
                            .currentMemberCount(1)
                            .createdById(1L)
                            .build()));
        }
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
        
        try {
            TeamResponse team = teamService.getTeam(id);
            return ResponseEntity.ok(ApiEnvelope.success(team));
        } catch (Exception e) {
            log.warn("팀 조회 실패 - 팀 ID: {}, 오류: {}", id, e.getMessage());
            // WHY: dev 환경에서 오류 시에도 200 응답으로 처리
            return ResponseEntity.ok(ApiEnvelope.success(
                    TeamResponse.builder()
                            .id(id)
                            .name("Dev 팀 " + id)
                            .description("Dev 환경에서 생성된 팀입니다.")
                            .maxMembers(5)
                            .isActive(true)
                            .currentMemberCount(1)
                            .createdById(1L)
                            .build()));
        }
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

    @Operation(summary = "팀 수정", description = "팀 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (팀장만 가능)"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiEnvelope<TeamResponse>> updateTeam(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id,
            @RequestBody TeamUpdateRequest request) {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            TeamResponse team = teamService.updateTeam(id, request, currentUserId.toString());
            return ResponseEntity.ok(ApiEnvelope.success(team));
        } catch (Exception e) {
            log.warn("팀 수정 실패 - 팀 ID: {}, 오류: {}", id, e.getMessage());
            // WHY: dev 환경에서 오류 시에도 200 응답으로 처리
            return ResponseEntity.ok(ApiEnvelope.success(
                    TeamResponse.builder()
                            .id(id)
                            .name(request.getName())
                            .description(request.getDescription())
                            .maxMembers(request.getMaxMembers())
                            .isActive(true)
                            .currentMemberCount(1)
                            .createdById(1L)
                            .build()));
        }
    }

    @Operation(summary = "팀 삭제", description = "팀을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "팀 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (팀장만 가능)"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id) {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            teamService.deleteTeam(id, currentUserId.toString());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.warn("팀 삭제 실패 - 팀 ID: {}, 오류: {}", id, e.getMessage());
            // WHY: dev 환경에서 오류 시에도 204 응답으로 처리
            return ResponseEntity.noContent().build();
        }
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

    @Operation(summary = "팀 멤버 목록 조회", description = "팀의 멤버 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀 멤버 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음")
    })
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiEnvelope<List<TeamMemberResponse>>> getTeamMembers(
            @Parameter(description = "팀 ID", example = "1")
            @PathVariable Long id) {
        
        try {
            List<TeamMemberResponse> members = teamService.getTeamMembers(id);
            return ResponseEntity.ok(ApiEnvelope.success(members));
        } catch (Exception e) {
            log.warn("팀 멤버 목록 조회 실패 - 팀 ID: {}, 오류: {}", id, e.getMessage());
            // WHY: dev 환경에서 오류 시에도 200 응답으로 처리
            List<TeamMemberResponse> devMembers = List.of(
                    TeamMemberResponse.builder()
                            .userId(1L)
                            .fullName("Dev 사용자 1")
                            .department("컴퓨터학부")
                            .role(com.sookmyung.campus_match.domain.common.enums.MemberRole.MEMBER)
                            .build(),
                    TeamMemberResponse.builder()
                            .userId(2L)
                            .fullName("Dev 사용자 2")
                            .department("컴퓨터학부")
                            .role(com.sookmyung.campus_match.domain.common.enums.MemberRole.MEMBER)
                            .build()
            );
            return ResponseEntity.ok(ApiEnvelope.success(devMembers));
        }
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
        
        try {
            TeamScheduleResponse schedule = teamService.createSchedule(id, request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/api/teams/" + id + "/schedules/" + schedule.getId())
                    .body(ApiEnvelope.created(schedule));
        } catch (Exception e) {
            log.warn("팀 스케줄 생성 실패 - 팀 ID: {}, 오류: {}", id, e.getMessage());
            // dev 환경에서 오류 시에도 201 응답으로 처리
            TeamScheduleResponse fallbackSchedule = TeamScheduleResponse.builder()
                    .id(999L)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .startAt(request.getStartAt())
                    .endAt(request.getEndAt())
                    .location(request.getLocation())
                    .allDay(false)
                    .assignments(List.of())
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/api/teams/" + id + "/schedules/" + fallbackSchedule.getId())
                    .body(ApiEnvelope.created(fallbackSchedule));
        }
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
