package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.TeamSchedule;
import com.sookmyung.campus_match.domain.team.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeamScheduleRepository extends JpaRepository<TeamSchedule, Long> {

    List<TeamSchedule> findBySecretaryId(Long secretaryId);
    
    List<TeamSchedule> findByCreatedById(Long createdById);
    
    @Query("SELECT ts FROM TeamSchedule ts WHERE " +
           "ts.secretary.team.id = :teamId AND " +
           "ts.startDate >= :startDate AND ts.endDate <= :endDate")
    List<TeamSchedule> findByTeamIdAndDateRange(@Param("teamId") Long teamId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ts FROM TeamSchedule ts WHERE ts.secretary.team.id = :teamId ORDER BY ts.startDate ASC")
    Page<TeamSchedule> findByTeamIdOrderByStartDate(@Param("teamId") Long teamId, Pageable pageable);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    List<TeamSchedule> findByTeam(Team team);

    // TeamService에서 호출하는 메서드들
    @Query("SELECT ts FROM TeamSchedule ts WHERE ts.team.id = :teamId ORDER BY ts.startTime ASC")
    List<TeamSchedule> findByTeam_IdOrderByStartTimeAsc(@Param("teamId") Long teamId);
}
