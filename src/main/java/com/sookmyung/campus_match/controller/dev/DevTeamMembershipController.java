package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dev 환경용 팀 멤버십 스텁 컨트롤러
 * 실제 팀 멤버십 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Profile("dev")
public class DevTeamMembershipController {

    private final CurrentUserResolver currentUserResolver;
    
    // In-memory 캐시
    private final ConcurrentHashMap<Long, TeamApplicationResponse> applications = new ConcurrentHashMap<>();
    private final AtomicLong applicationIdCounter = new AtomicLong(1);

    /**
     * Dev 환경용 팀 가입 신청 스텁
     */
    @PostMapping("/{teamId}/join")
    public ResponseEntity<ApiEnvelope<TeamApplicationResponse>> joinTeam(
            @PathVariable Long teamId) {
        log.info("Dev 환경 - 팀 가입 신청 스텁 호출: teamId={}", teamId);
        
        Long applicationId = applicationIdCounter.getAndIncrement();
        Long userId = currentUserResolver.currentUserId();
        
        TeamApplicationResponse response = TeamApplicationResponse.builder()
                .applicationId(applicationId)
                .teamId(teamId)
                .userId(userId)
                .status("APPLIED")
                .createdAt(Instant.now().toString())
                .build();
        
        applications.put(applicationId, response);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/teams/" + teamId + "/applications/" + applicationId)
                .body(ApiEnvelope.created(response));
    }

    /**
     * Dev 환경용 팀 가입 신청 목록 조회 스텁
     */
    @GetMapping("/{teamId}/applications")
    public ResponseEntity<ApiEnvelope<List<TeamApplicationResponse>>> getTeamApplications(
            @PathVariable Long teamId) {
        log.info("Dev 환경 - 팀 가입 신청 목록 조회 스텁 호출: teamId={}", teamId);
        
        List<TeamApplicationResponse> applicationList = applications.values().stream()
                .filter(app -> app.teamId.equals(teamId))
                .toList();
        
        return ResponseEntity.ok(ApiEnvelope.success(applicationList));
    }

    /**
     * Dev 환경용 팀 가입 신청 삭제 스텁
     */
    @DeleteMapping("/{teamId}/applications/{id}")
    public ResponseEntity<Void> deleteTeamApplication(
            @PathVariable Long teamId,
            @PathVariable Long id) {
        log.info("Dev 환경 - 팀 가입 신청 삭제 스텁 호출: teamId={}, applicationId={}", teamId, id);
        
        applications.remove(id);
        
        return ResponseEntity.noContent().build();
    }

    // 내부 DTO 클래스들
    public static class TeamApplicationResponse {
        public Long applicationId;
        public Long teamId;
        public Long userId;
        public String status;
        public String createdAt;

        public static TeamApplicationResponseBuilder builder() {
            return new TeamApplicationResponseBuilder();
        }

        public static class TeamApplicationResponseBuilder {
            private TeamApplicationResponse response = new TeamApplicationResponse();

            public TeamApplicationResponseBuilder applicationId(Long applicationId) {
                response.applicationId = applicationId;
                return this;
            }

            public TeamApplicationResponseBuilder teamId(Long teamId) {
                response.teamId = teamId;
                return this;
            }

            public TeamApplicationResponseBuilder userId(Long userId) {
                response.userId = userId;
                return this;
            }

            public TeamApplicationResponseBuilder status(String status) {
                response.status = status;
                return this;
            }

            public TeamApplicationResponseBuilder createdAt(String createdAt) {
                response.createdAt = createdAt;
                return this;
            }

            public TeamApplicationResponse build() {
                return response;
            }
        }
    }
}

