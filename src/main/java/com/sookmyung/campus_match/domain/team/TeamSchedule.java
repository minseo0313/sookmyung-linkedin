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

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 도메인 메서드들
    public void updateBasics(String title, String description, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        if (startAt != null) {
            this.startAt = startAt;
        }
        if (endAt != null) {
            this.endAt = endAt;
        }
    }

    public void reschedule(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // 빌더 메서드 추가
    public static class TeamScheduleBuilder {
        public TeamScheduleBuilder team(Team team) {
            this.team = team;
            return this;
        }

        public TeamScheduleBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TeamScheduleBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TeamScheduleBuilder startAt(LocalDateTime startAt) {
            this.startAt = startAt;
            return this;
        }

        public TeamScheduleBuilder endAt(LocalDateTime endAt) {
            this.endAt = endAt;
            return this;
        }

        public TeamScheduleBuilder location(String location) {
            this.location = location;
            return this;
        }
    }
}
