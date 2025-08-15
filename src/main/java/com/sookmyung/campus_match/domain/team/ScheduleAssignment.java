package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.AssignmentStatus;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false)
    private AssignmentStatus assignmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private TeamSchedule schedule;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "title", length = 255)
    private String title;

    // 호환성 메서드들
    public User getAssignee() {
        return this.assignedTo;
    }

    public AssignmentStatus getStatus() {
        return this.assignmentStatus;
    }

    public String getTitle() {
        return this.title;
    }

    // 빌더 메서드 추가
    public static class ScheduleAssignmentBuilder {
        public ScheduleAssignmentBuilder assignee(User assignee) {
            this.assignedTo = assignee;
            return this;
        }

        public ScheduleAssignmentBuilder status(AssignmentStatus status) {
            this.assignmentStatus = status;
            return this;
        }
    }
}
