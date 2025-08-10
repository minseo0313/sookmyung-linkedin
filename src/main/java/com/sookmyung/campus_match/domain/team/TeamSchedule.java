package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 팀 일정 엔티티
 * - 팀 비서 캘린더의 단일 일정
 * - 일정별 개인 업무 할당은 ScheduleAssignment로 관리
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "team_schedules",
        indexes = {
                @Index(name = "idx_team_schedules_team_id", columnList = "team_id"),
                @Index(name = "idx_team_schedules_start_at", columnList = "start_at"),
                @Index(name = "idx_team_schedules_end_at", columnList = "end_at"),
                @Index(name = "idx_team_schedules_created", columnList = "created_at")
        }
)
public class TeamSchedule extends BaseEntity {

    /** 소속 팀 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_team_schedules_team"))
    private Team team;

    /** 일정 제목 */
    @Column(nullable = false, length = 120)
    private String title;

    /** 일정 설명(선택) */
    @Column(length = 500)
    private String description;

    /** 시작/종료 시각 */
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    /** 종일 여부 */
    @Column(nullable = false)
    @Builder.Default
    private boolean allDay = false;

    /** 장소(선택) */
    @Column(length = 120)
    private String location;

    /** 개인 업무 할당 목록 */
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScheduleAssignment> assignments = new ArrayList<>();

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setTeam(Team team) {
        this.team = team;
    }

    public void addAssignment(ScheduleAssignment assignment) {
        if (assignment == null) return;
        assignments.add(assignment);
        assignment.setSchedule(this);
    }

    public void removeAssignment(ScheduleAssignment assignment) {
        if (assignment == null) return;
        assignments.remove(assignment);
        assignment.setSchedule(null);
    }

    /* ==========================
       상태 변경/유틸 메서드
       ========================== */

    public void updateBasics(String title, String description, String location, Boolean allDay) {
        if (title != null && !title.isBlank()) this.title = title;
        if (description != null) this.description = description;
        if (location != null) this.location = location;
        if (allDay != null) this.allDay = allDay;
    }

    public void reschedule(LocalDateTime startAt, LocalDateTime endAt) {
        validatePeriod(startAt, endAt);
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        if (otherStart == null || otherEnd == null) return false;
        return !otherEnd.isBefore(this.startAt) && !otherStart.isAfter(this.endAt);
    }

    private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null || endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("Invalid period: startAt/endAt");
        }
    }
}
