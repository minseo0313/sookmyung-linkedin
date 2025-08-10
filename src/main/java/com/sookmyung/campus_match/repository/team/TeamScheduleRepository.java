package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.TeamSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamScheduleRepository extends JpaRepository<TeamSchedule, Long> {

    // 팀별 일정 목록 (시작 시간 오름차순)
    List<TeamSchedule> findByTeam_IdOrderByStartAtAsc(Long teamId);

    // 팀별 일정 페이징
    Page<TeamSchedule> findByTeam_Id(Long teamId, Pageable pageable);

    // 기간 내 일정
    List<TeamSchedule> findByTeam_IdAndStartAtBetweenOrderByStartAtAsc(Long teamId,
                                                                       LocalDateTime from,
                                                                       LocalDateTime to);

    // 앞으로 다가오는 일정 Top N (예: 캘린더 위젯)
    List<TeamSchedule> findTop10ByTeam_IdAndStartAtGreaterThanEqualOrderByStartAtAsc(Long teamId,
                                                                                     LocalDateTime from);

    // 제목 키워드 검색 + 페이징
    Page<TeamSchedule> findByTeam_IdAndTitleContainingIgnoreCase(Long teamId,
                                                                 String keyword,
                                                                 Pageable pageable);

    // 기간 겹치는 일정 조회 (overlap 조건: !(end < start || start > end))
    @Query("""
           select s
             from TeamSchedule s
            where s.team.id = :teamId
              and not (s.endAt < :startAt or s.startAt > :endAt)
            order by s.startAt asc
           """)
    List<TeamSchedule> findOverlappingSchedules(Long teamId,
                                                LocalDateTime startAt,
                                                LocalDateTime endAt);

    // 팀별 일정 개수/일괄 삭제
    long countByTeam_Id(Long teamId);
    void deleteByTeam_Id(Long teamId);
}
