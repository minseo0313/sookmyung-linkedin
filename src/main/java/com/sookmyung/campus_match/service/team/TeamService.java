package com.sookmyung.campus_match.service.team;

import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.team.TeamAcceptRequest;
import com.sookmyung.campus_match.dto.team.TeamMemberResponse;
import com.sookmyung.campus_match.dto.team.TeamResponse;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.team.TeamMemberRepository;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public TeamResponse acceptTeam(Long teamId, TeamAcceptRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // TODO: 팀 매칭 확정 로직 구현 필요
        // - 지원자들을 팀 멤버로 추가
        // - 팀 상태 업데이트
        // - 팀 비서 캘린더 자동 생성

        // TODO: 팀 매칭 확정 로직 구현 필요

        return TeamResponse.from(team);
    }

    public List<TeamMemberResponse> getTeamMembers(Long teamId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ApiException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));

        // 팀 멤버인지 확인
        boolean isMember = teamMemberRepository.existsByTeamAndUser(team, user);
        if (!isMember) {
            throw new ApiException(ErrorCode.TEAM_ACCESS_DENIED, "팀 멤버만 접근할 수 있습니다.");
        }

        List<TeamMember> members = teamMemberRepository.findByTeam(team);
        
        return members.stream()
                .map(TeamMemberResponse::from)
                .collect(Collectors.toList());
    }
}
