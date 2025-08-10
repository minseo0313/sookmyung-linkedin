package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.team.enum_.AssignmentStatus;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팀 일정 내 개인 업무 할당(Task) 엔티티
 * - TeamSchedule(일정) 단위로 하위 업무를 사용자에게 배정
 * - 상태: TODO / DOING / DONE
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "schedule_assignments",
        indexes = {
                @Index(name = "idx_schedule_assignments_schedule_id", columnList = "schedule_id"),
                @Index(name = "idx_schedule_assignments_assignee_id", columnList = "assignee_id"),
                @Index(name = "idx_schedule_assignments_status", columnList = "status"),
                @Index(name = "idx_schedule_assignments_created", columnList = "created_at")
        }
)
public class ScheduleAssignment extends BaseEntity {

    /** 소속 일정 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_schedule_assignments_schedule"))
    private TeamSchedule schedule;

    /** 담당자 (팀원 User) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignee_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_schedule_assignments_assignee"))
    private User assignee;

    /** 업무 제목 */
    @Column(nullable = false, length = 120)
    private String title;

    /** 업무 설명(선택) */
    @Column(length = 1000)
    private String description;

    /** 상태 (TODO/DOING/DONE) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.TODO;

    /** 마감 기한(선택) - 일정 범위 내 세부 마감이 필요할 때 사용 */
    @Column(name = "due_at")
    private LocalDateTime dueAt;

    /** 진행률(0~100, 선택) */
    @Column(name = "progress_pct")
    private Integer progressPct;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setSchedule(TeamSchedule schedule) {
        this.schedule = schedule;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /* ==========================
       도메인/상태 변경 메서드
       ========================== */

    public void updateBasics(String title, String description, LocalDateTime dueAt) {
        if (title != null && !title.isBlank()) this.title = title;
        if (description != null) this.description = description;
        this.dueAt = dueAt; // null 허용
    }

    public void changeStatus(AssignmentStatus status) {
        if (status != null) this.status = status;
        if (status == AssignmentStatus.DONE) this.progressPct = 100;
    }

    /** 담당자 변경 */
    public void reassign(User newAssignee) {
        if (newAssignee != null) this.assignee = newAssignee;
    }

    /** 진행률 갱신 (0~100 범위 클램핑) */
    public void updateProgress(Integer pct) {
        if (pct == null) {
            this.progressPct = null;
            return;
        }
        int v = Math.max(0, Math.min(100, pct));
        this.progressPct = v;
        if (v >= 100) this.status = AssignmentStatus.DONE;
        else if (v > 0 && this.status == AssignmentStatus.TODO) this.status = AssignmentStatus.DOING;
    }
}
