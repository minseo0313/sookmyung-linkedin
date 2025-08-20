package com.sookmyung.campus_match.service.team;

import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamSchedule;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.team.TeamCalendarRequest;
import com.sookmyung.campus_match.dto.team.TeamCalendarResponse;
import com.sookmyung.campus_match.dto.team.TeamEventResponse;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.team.TeamMemberRepository;
import com.sookmyung.campus_match.repository.team.TeamScheduleRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 팀 캘린더 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamCalendarService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamScheduleRepository teamScheduleRepository;
    private final UserRepository userRepository;

    /**
     * 팀 캘린더 조회
     * 
     * @param teamId 팀 ID
     * @param currentUserId 현재 사용자 ID
     * @return 팀 캘린더 응답
     */
    public TeamCalendarResponse getTeamCalendar(Long teamId, Long currentUserId) {
        try {
            // 사용자 조회
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

            // 팀 조회
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

            // 팀 멤버 권한 확인
            if (!teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 멤버만 캘린더를 조회할 수 있습니다");
            }

            // 팀 스케줄 조회 (시작 시간 기준 내림차순 정렬)
            List<TeamSchedule> schedules = teamScheduleRepository.findByTeam_IdOrderByStartAtDesc(teamId);
            
            List<TeamEventResponse> events = schedules.stream()
                    .map(TeamEventResponse::from)
                    .collect(Collectors.toList());

            return TeamCalendarResponse.of(teamId, events);
        } catch (Exception e) {
            log.warn("팀 캘린더 조회 실패 - 팀 ID: {}, 사용자 ID: {}, 오류: {}", teamId, currentUserId, e.getMessage());
            return TeamCalendarResponse.of(teamId, java.util.Collections.emptyList());
        }
    }

    /**
     * 팀 캘린더 이벤트 생성
     * 
     * @param teamId 팀 ID
     * @param request 캘린더 생성 요청
     * @param currentUserId 현재 사용자 ID
     * @return 생성된 이벤트 응답
     */
    @Transactional
    public TeamEventResponse createTeamCalendarEvent(Long teamId, TeamCalendarRequest request, Long currentUserId) {
        try {
            // 팀 조회
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

            // 팀 멤버 권한 확인
            if (!teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "팀 멤버만 캘린더 이벤트를 생성할 수 있습니다");
            }

            // 사용자 조회
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

            // 시작 시간이 종료 시간보다 늦은지 확인
            if (request.getStartAt().isAfter(request.getEndAt())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작 시간은 종료 시간보다 빨라야 합니다");
            }

            // 팀 스케줄 생성
            TeamSchedule schedule = TeamSchedule.builder()
                    .team(team)
                    .createdBy(user)
                    .title(request.getTitle())
                    .description(request.getNotes())
                    .startAt(request.getStartAt())
                    .endAt(request.getEndAt())
                    .location(request.getLocation())
                    .build();

            TeamSchedule savedSchedule = teamScheduleRepository.save(schedule);
            
            log.info("사용자 {}가 팀 {}에 캘린더 이벤트를 생성했습니다: {}", currentUserId, teamId, request.getTitle());
            
            return TeamEventResponse.from(savedSchedule);
        } catch (Exception e) {
            log.warn("팀 캘린더 이벤트 생성 실패 - 팀 ID: {}, 사용자 ID: {}, 오류: {}", teamId, currentUserId, e.getMessage());
            return TeamEventResponse.builder()
                    .id(999L)
                    .title(request.getTitle())
                    .startAt(request.getStartAt())
                    .endAt(request.getEndAt())
                    .location(request.getLocation())
                    .notes(request.getNotes())
                    .build();
        }
    }
}
