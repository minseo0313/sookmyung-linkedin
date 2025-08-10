package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.team.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // 팀명으로 검색
    List<Team> findByNameContainingIgnoreCase(String keyword);

    // 특정 생성자의 팀 목록
    List<Team> findByCreatedBy_Id(Long creatorId);

    // 특정 멤버가 속한 팀 목록 (TeamMember 엔티티 조인 없이)
    List<Team> findByMembers_User_Id(Long userId);

    // 팀명 중복 여부
    boolean existsByNameIgnoreCase(String name);

    // 팀 단건 조회 + 멤버까지 즉시 로딩 (필요 시 fetch join)
    Optional<Team> findWithMembersById(Long teamId);
}
