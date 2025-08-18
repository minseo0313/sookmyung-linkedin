package com.sookmyung.campus_match.domain.post;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import com.sookmyung.campus_match.domain.team.Team;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "post_applications",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "applicant_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostApplication extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    // 도메인 메서드들
    public void accept() {
        this.status = ApplicationStatus.ACCEPTED;
        this.decidedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
        this.decidedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    // 호환성 메서드들
    public String getMessage() {
        return this.message;
    }

    public Long getPostId() {
        return this.post != null ? this.post.getId() : null;
    }

    public Long getApplicantId() {
        return this.applicant != null ? this.applicant.getId() : null;
    }
}
