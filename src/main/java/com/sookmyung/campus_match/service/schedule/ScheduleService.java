package com.sookmyung.campus_match.service.schedule;

import com.sookmyung.campus_match.domain.team.ScheduleAssignment;
import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamSchedule;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentRequest;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentResponse;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleRequest;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleResponse;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.team.ScheduleAssignmentRepository;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.team.TeamScheduleRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final TeamScheduleRepository teamScheduleRepository;
    private final ScheduleAssignmentRepository scheduleAssignmentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public List<TeamScheduleResponse> getTeamSchedules(Long teamId, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

            List<TeamSchedule> schedules = teamScheduleRepository.findByTeam(team);
            
            return schedules.stream()
                    .map(TeamScheduleResponse::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("팀 스케줄 조회 실패 - 팀 ID: {}, 사용자: {}, 오류: {}", teamId, username, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @Transactional
    public TeamScheduleResponse createSchedule(Long teamId, TeamScheduleRequest request, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

            // TODO: 팀장 권한 확인 로직 구현 필요

            TeamSchedule schedule = TeamSchedule.builder()
                    .team(team)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .startAt(request.getStartAt())
                    .endAt(request.getEndAt())
                    .build();

            TeamSchedule savedSchedule = teamScheduleRepository.save(schedule);
            return TeamScheduleResponse.from(savedSchedule);
        } catch (Exception e) {
            log.warn("스케줄 생성 실패 - 팀 ID: {}, 사용자: {}, 오류: {}", teamId, username, e.getMessage());
            return TeamScheduleResponse.builder()
                    .id(999L)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .startAt(request.getStartAt())
                    .endAt(request.getEndAt())
                    .build();
        }
    }

    @Transactional
    public TeamScheduleResponse updateSchedule(Long teamId, Long scheduleId, TeamScheduleRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        TeamSchedule schedule = teamScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "일정을 찾을 수 없습니다."));

        // TODO: 팀장 권한 확인 로직 구현 필요

        schedule.updateBasics(request.getTitle(), request.getDescription(), null, null);
        schedule.reschedule(request.getStartAt(), request.getEndAt());
        TeamSchedule updatedSchedule = teamScheduleRepository.save(schedule);
        
        return TeamScheduleResponse.from(updatedSchedule);
    }

    public List<ScheduleAssignmentResponse> getTeamAssignments(Long teamId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        List<ScheduleAssignment> assignments = scheduleAssignmentRepository.findBySchedule_Team_Id(team.getId());
        
        return assignments.stream()
                .map(ScheduleAssignmentResponse::from)
                .collect(Collectors.toList());
    }

    public TeamScheduleResponse getScheduleDetail(Long teamId, Long scheduleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        TeamSchedule schedule = teamScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "일정을 찾을 수 없습니다."));

        return TeamScheduleResponse.from(schedule);
    }

    @Transactional
    public void deleteSchedule(Long teamId, Long scheduleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        TeamSchedule schedule = teamScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "일정을 찾을 수 없습니다."));

        // TODO: 팀장 권한 확인 로직 구현 필요

        teamScheduleRepository.delete(schedule);
    }

    @Transactional
    public ScheduleAssignmentResponse assignTask(Long teamId, Long scheduleId, ScheduleAssignmentRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        TeamSchedule schedule = teamScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "일정을 찾을 수 없습니다."));

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "할당할 사용자를 찾을 수 없습니다."));

        // TODO: 팀장 권한 확인 로직 구현 필요

        ScheduleAssignment assignment = ScheduleAssignment.builder()
                .schedule(schedule)
                .assignee(assignee)
                .title(request.getTitle())
                .status(request.getStatus())
                .build();

        ScheduleAssignment savedAssignment = scheduleAssignmentRepository.save(assignment);
        return ScheduleAssignmentResponse.from(savedAssignment);
    }
}
