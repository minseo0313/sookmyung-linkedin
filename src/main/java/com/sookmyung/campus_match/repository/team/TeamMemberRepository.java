package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.TeamMember;
import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByUser_Id(Long userId);
    
    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.user.id = :userId")
    Optional<TeamMember> findByTeam_IdAndUser_Id(@Param("teamId") Long teamId, @Param("userId") Long userId);
    
    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);
    
    long countByTeam_Id(Long teamId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    List<TeamMember> findByTeam(Team team);
    
    boolean existsByTeamAndUser(Team team, User user);
    
    boolean existsByTeam_IdAndRole(Long teamId, MemberRole role);
    
    void deleteByTeam_IdAndUser_Id(Long teamId, Long userId);
}
