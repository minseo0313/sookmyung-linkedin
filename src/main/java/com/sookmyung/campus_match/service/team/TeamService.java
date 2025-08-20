package com.sookmyung.campus_match.service.team;

import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.team.TeamSchedule;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.CreatedFrom;
import com.sookmyung.campus_match.domain.common.enums.AssignmentStatus;
import com.sookmyung.campus_match.dto.team.TeamResponse;
import com.sookmyung.campus_match.dto.team.TeamAcceptRequest;
import com.sookmyung.campus_match.dto.team.TeamUpdateRequest;
import com.sookmyung.campus_match.dto.team.TeamMemberResponse;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleRequest;
import com.sookmyung.campus_match.dto.schedule.TeamScheduleResponse;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentRequest;
import com.sookmyung.campus_match.dto.schedule.ScheduleAssignmentResponse;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.team.TeamMemberRepository;
import com.sookmyung.campus_match.repository.team.TeamScheduleRepository;
import com.sookmyung.campus_match.repository.team.ScheduleAssignmentRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamScheduleRepository teamScheduleRepository;
    private final ScheduleAssignmentRepository scheduleAssignmentRepository;
    private final UserRepository userRepository;

    /**
     * 팀 생성
     */
    @Transactional
    public TeamResponse createTeam(String teamName, String description, Integer maxMembers, 
                                 Long postId, String username) {
        User user = userRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = Team.builder()
                .teamName(teamName)
                .description(description)
                .createdBy(user)
                .createdFrom(CreatedFrom.POST)
                .maxMembers(maxMembers)
                .isActive(true)
                .build();

        Team savedTeam = teamRepository.save(team);
        
        // 생성자를 팀장으로 추가
        TeamMember leader = TeamMember.builder()
                .team(savedTeam)
                .user(user)
                .role(MemberRole.LEADER)
                .build();
        teamMemberRepository.save(leader);

        return TeamResponse.from(savedTeam);
    }

    /**
     * 팀 수정
     */
    @Transactional
    public TeamResponse updateTeam(Long teamId, TeamUpdateRequest request, String username) {
        User user = userRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀장만 팀을 수정할 수 있음
        if (!team.isLeader(user)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 팀을 수정할 수 있습니다.");
        }

        team.setTeamName(request.getName());
        team.setDescription(request.getDescription());
        team.setMaxMembers(request.getMaxMembers());

        Team savedTeam = teamRepository.save(team);
        return TeamResponse.from(savedTeam);
    }

    /**
     * 팀 삭제
     */
    @Transactional
    public void deleteTeam(Long teamId, String username) {
        User user = userRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀장만 팀을 삭제할 수 있음
        if (!team.isLeader(user)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 팀을 삭제할 수 있습니다.");
        }

        teamRepository.delete(team);
        log.info("팀 {} 삭제됨", teamId);
    }

    /**
     * 팀 조회
     */
    public TeamResponse getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));
        
        return TeamResponse.from(team);
    }

    /**
     * 팀 목록 조회
     */
    public Page<TeamResponse> getTeams(Pageable pageable) {
        Page<Team> teams = teamRepository.findAll(pageable);
        return teams.map(TeamResponse::from);
    }

    /**
     * 사용자의 팀 목록 조회
     */
    public List<TeamResponse> getUserTeams(String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<Team> teams = teamRepository.findByMembers_User_Id(user.getId());
        return teams.stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 팀 멤버 목록 조회
     */
    public List<TeamMemberResponse> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));
        
        List<TeamMember> members = teamMemberRepository.findByTeam(team);
        return members.stream()
                .map(TeamMemberResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 팀 멤버 추가
     */
    @Transactional
    public void addMember(Long teamId, Long userId, String username) {
        User currentUser = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀장만 멤버를 추가할 수 있음
        if (!team.isLeader(currentUser)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 멤버를 추가할 수 있습니다.");
        }

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "추가할 사용자를 찾을 수 없습니다."));

        if (team.isMember(newMember)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "이미 팀 멤버입니다.");
        }

        if (!team.canAddMember()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "팀 인원이 가득 찼습니다.");
        }

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(newMember)
                .role(MemberRole.MEMBER)
                .build();

        teamMemberRepository.save(member);
        log.info("팀 {}에 사용자 {} 추가됨", teamId, userId);
    }

    /**
     * 팀 멤버 제거
     */
    @Transactional
    public void removeMember(Long teamId, Long userId, String username) {
        User currentUser = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀장만 멤버를 제거할 수 있음
        if (!team.isLeader(currentUser)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 멤버를 제거할 수 있습니다.");
        }

        TeamMember member = teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "팀 멤버를 찾을 수 없습니다."));

        // 팀장은 제거할 수 없음
        if (member.getUser().getId().equals(team.getCreatedBy().getId())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "팀장은 제거할 수 없습니다.");
        }

        teamMemberRepository.delete(member);
        log.info("팀 {}에서 사용자 {} 제거됨", teamId, userId);
    }

    /**
     * 팀 스케줄 생성
     */
    @Transactional
    public TeamScheduleResponse createSchedule(Long teamId, TeamScheduleRequest request, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀장만 스케줄을 생성할 수 있음
        if (!team.isLeader(user)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 스케줄을 생성할 수 있습니다.");
        }

        TeamSchedule schedule = TeamSchedule.builder()
                .team(team)
                .title(request.getTitle())
                .description(request.getDescription())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .location(request.getLocation())
                .build();

        TeamSchedule savedSchedule = teamScheduleRepository.save(schedule);
        return TeamScheduleResponse.from(savedSchedule);
    }

    /**
     * 팀 스케줄 조회
     */
    public List<TeamScheduleResponse> getTeamSchedules(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        List<TeamSchedule> schedules = teamScheduleRepository.findByTeam_IdOrderByStartAtDesc(teamId);
        return schedules.stream()
                .map(TeamScheduleResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 스케줄 할당
     */
    @Transactional
    public ScheduleAssignmentResponse assignSchedule(Long scheduleId, ScheduleAssignmentRequest request, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        TeamSchedule schedule = teamScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "스케줄을 찾을 수 없습니다."));

        // 팀장만 할당할 수 있음
        if (!schedule.getTeam().isLeader(user)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 스케줄을 할당할 수 있습니다.");
        }

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "할당할 사용자를 찾을 수 없습니다."));

        // 이미 할당된 사용자인지 확인
        if (scheduleAssignmentRepository.existsBySchedule_IdAndAssignee_Id(scheduleId, request.getAssigneeId())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "이미 할당된 사용자입니다.");
        }

        var assignment = com.sookmyung.campus_match.domain.team.ScheduleAssignment.builder()
                .schedule(schedule)
                .assignee(assignee)
                .status(AssignmentStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .build();

        var savedAssignment = scheduleAssignmentRepository.save(assignment);
        return ScheduleAssignmentResponse.from(savedAssignment);
    }

    /**
     * 팀 비활성화
     */
    @Transactional
    public void deactivateTeam(Long teamId, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀장만 팀을 비활성화할 수 있음
        if (!team.isLeader(user)) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀장만 팀을 비활성화할 수 있습니다.");
        }

        team.deactivate();
        teamRepository.save(team);
        log.info("팀 {} 비활성화됨", teamId);
    }
}
