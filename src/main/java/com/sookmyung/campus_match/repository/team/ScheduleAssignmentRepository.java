package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.ScheduleAssignment;
import com.sookmyung.campus_match.domain.team.enum_.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleAssignmentRepository extends JpaRepository<ScheduleAssignment, Long> {

    // 일정 단위 업무 목록
    List<ScheduleAssignment> findBySchedule_Id(Long scheduleId);
    Page<ScheduleAssignment> findBySchedule_Id(Long scheduleId, Pageable pageable);

    // 담당자(팀원) 기준 업무 목록
    List<ScheduleAssignment> findByAssignee_Id(Long userId);
    Page<ScheduleAssignment> findByAssignee_Id(Long userId, Pageable pageable);

    // 상태별 필터
    List<ScheduleAssignment> findBySchedule_IdAndStatus(Long scheduleId, AssignmentStatus status);
    List<ScheduleAssignment> findByAssignee_IdAndStatusOrderByDueAtAsc(Long userId, AssignmentStatus status);

    // 마감 임박/앞으로 할 일 (Top N 예시)
    List<ScheduleAssignment> findTop20ByAssignee_IdOrderByDueAtAsc(Long userId);

    // 일정별 개수 / 일괄 삭제
    long countBySchedule_Id(Long scheduleId);
    void deleteBySchedule_Id(Long scheduleId);

    // Overdue(마감 지남 + 완료 아님) — 팀 단위 조회
    @Query("""
           select a
             from ScheduleAssignment a
            where a.schedule.team.id = :teamId
              and a.dueAt is not null
              and a.dueAt < :now
              and a.status <> :doneStatus
            order by a.dueAt asc
           """)
    List<ScheduleAssignment> findOverdueByTeam(Long teamId, LocalDateTime now, AssignmentStatus doneStatus);
}
