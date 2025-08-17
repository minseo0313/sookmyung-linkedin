package com.sookmyung.campus_match.repository.team;

import com.sookmyung.campus_match.domain.team.ScheduleAssignment;
import com.sookmyung.campus_match.domain.common.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleAssignmentRepository extends JpaRepository<ScheduleAssignment, Long> {

    List<ScheduleAssignment> findBySchedule_Id(Long scheduleId);
    
    List<ScheduleAssignment> findByAssignedTo_Id(Long assignedToId);
    
    List<ScheduleAssignment> findBySchedule_IdAndAssignmentStatus(Long scheduleId, AssignmentStatus status);
    
    @Query("SELECT sa FROM ScheduleAssignment sa WHERE " +
           "sa.schedule.id = :scheduleId AND " +
           "(:status IS NULL OR sa.assignmentStatus = :status)")
    List<ScheduleAssignment> findBySchedule_IdAndStatus(@Param("scheduleId") Long scheduleId,
                                                      @Param("status") AssignmentStatus status);
    
    long countBySchedule_Id(Long scheduleId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Query("SELECT sa FROM ScheduleAssignment sa WHERE sa.schedule.team.id = :teamId")
    List<ScheduleAssignment> findBySchedule_Team_Id(@Param("teamId") Long teamId);

    // TeamService에서 호출하는 메서드들
    @Query("SELECT sa FROM ScheduleAssignment sa WHERE sa.schedule.id = :scheduleId AND sa.assignee.id = :assigneeId")
    boolean existsBySchedule_IdAndAssignee_Id(@Param("scheduleId") Long scheduleId, @Param("assigneeId") Long assigneeId);
}
