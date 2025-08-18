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

    List<TeamSchedule> findByCreatedById(Long createdById);
    
    @Query("SELECT ts FROM TeamSchedule ts WHERE " +
           "ts.team.id = :teamId AND " +
           "ts.startAt >= :startAt AND ts.endAt <= :endAt " +
           "ORDER BY ts.startAt DESC")
    List<TeamSchedule> findByTeam_IdAndRangeOrderByStartAtDesc(@Param("teamId") Long teamId,
                                                              @Param("startAt") LocalDateTime startAt,
                                                              @Param("endAt") LocalDateTime endAt);
    
    @Query("SELECT ts FROM TeamSchedule ts WHERE ts.team.id = :teamId ORDER BY ts.startAt ASC")
    Page<TeamSchedule> findByTeam_IdOrderByStartAtAsc(@Param("teamId") Long teamId, Pageable pageable);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    List<TeamSchedule> findByTeam(Team team);

    // TeamCalendarService에서 사용하는 메서드 (startAt 기준 내림차순 정렬)
    @Query("SELECT ts FROM TeamSchedule ts WHERE ts.team.id = :teamId ORDER BY ts.startAt DESC")
    List<TeamSchedule> findByTeam_IdOrderByStartAtDesc(@Param("teamId") Long teamId);
}
