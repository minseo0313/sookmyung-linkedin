package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "schedule_title", nullable = false, length = 255)
    private String scheduleTitle;

    @Lob
    @Column(name = "schedule_description", columnDefinition = "TEXT")
    private String scheduleDescription;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretary_id", nullable = false)
    private TeamSecretary secretary;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 호환성 메서드들
    public String getTitle() {
        return this.scheduleTitle;
    }

    public String getDescription() {
        return this.scheduleDescription;
    }

    public LocalDateTime getStartAt() {
        return this.startDate;
    }

    public LocalDateTime getEndAt() {
        return this.endDate;
    }

    public boolean isAllDay() {
        return false; // 기본값
    }

    public String getLocation() {
        return null; // 기본값
    }

    // 도메인 메서드들
    public void updateBasics(String title, String description, LocalDateTime startAt, LocalDateTime endAt) {
        this.scheduleTitle = title;
        this.scheduleDescription = description;
        if (startAt != null) this.startDate = startAt;
        if (endAt != null) this.endDate = endAt;
    }

    public void reschedule(LocalDateTime startAt, LocalDateTime endAt) {
        this.startDate = startAt;
        this.endDate = endAt;
    }

    // 빌더 메서드 추가
    public static class TeamScheduleBuilder {
        public TeamScheduleBuilder team(Team team) {
            this.team = team;
            return this;
        }

        public TeamScheduleBuilder title(String title) {
            this.scheduleTitle = title;
            return this;
        }

        public TeamScheduleBuilder description(String description) {
            this.scheduleDescription = description;
            return this;
        }

        public TeamScheduleBuilder startAt(LocalDateTime startAt) {
            this.startDate = startAt;
            return this;
        }

        public TeamScheduleBuilder endAt(LocalDateTime endAt) {
            this.endDate = endAt;
            return this;
        }
    }
}
