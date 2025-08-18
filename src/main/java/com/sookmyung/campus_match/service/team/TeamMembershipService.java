package com.sookmyung.campus_match.service.team;

import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import com.sookmyung.campus_match.dto.team.TeamMembershipResponse;
import com.sookmyung.campus_match.repository.team.TeamRepository;
import com.sookmyung.campus_match.repository.team.TeamMemberRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 팀 멤버십 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMembershipService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    /**
     * 팀 초대 수락하여 멤버가 됨
     * 
     * @param teamId 팀 ID
     * @param currentUserId 현재 사용자 ID
     * @return 팀 멤버십 응답
     */
    @Transactional
    public TeamMembershipResponse acceptInvite(Long teamId, Long currentUserId) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

        // 사용자 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        // 이미 멤버인지 확인
        if (teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 팀 멤버입니다");
        }

        // TODO: 추후 초대 검증 로직 추가
        // 현재는 단순 정책으로 초대 엔티티가 없어도 합류 허용
        // 향후 TeamInvitation 엔티티를 추가하여 초대 상태 검증 로직 구현 예정

        // 팀 인원 제한 확인 (Repository를 활용한 정확한 멤버 수 계산)
        if (team.hasMemberLimit()) {
            long currentMemberCount = teamMemberRepository.countByTeam_Id(teamId);
            if (currentMemberCount >= team.getMaxMembers()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "팀 인원이 가득 찼습니다");
            }
        }

        // 팀 멤버 생성
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role(MemberRole.MEMBER)
                .build();

        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);
        
        log.info("사용자 {}가 팀 {}에 멤버로 합류했습니다", currentUserId, teamId);
        
        return TeamMembershipResponse.from(savedTeamMember);
    }
}
