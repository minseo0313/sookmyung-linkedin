package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.team.enum_.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // 특정 팀의 전체 멤버 조회
    List<TeamMember> findByTeam_Id(Long teamId);

    // 특정 유저가 속한 팀 멤버 정보
    Optional<TeamMember> findByTeam_IdAndUser_Id(Long teamId, Long userId);

    // 역할별 멤버 조회 (예: 팀장만)
    List<TeamMember> findByTeam_IdAndRole(Long teamId, MemberRole role);

    // 특정 팀에서 해당 역할을 가진 멤버 존재 여부
    boolean existsByTeam_IdAndRole(Long teamId, MemberRole role);

    // 특정 유저가 속한 모든 팀 목록
    List<TeamMember> findByUser_Id(Long userId);

    // 특정 팀에서 멤버 삭제
    void deleteByTeam_IdAndUser_Id(Long teamId, Long userId);
}
