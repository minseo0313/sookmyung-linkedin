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

    @Lob
    @Column(name = "application_text", columnDefinition = "TEXT")
    private String applicationText;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    // TODO: DB 마이그레이션 시 제거할 필드들
    // @Column(name = "user_id") - applicant_id와 중복, applicant_id만 사용

    // 도메인 메서드들
    public void accept() {
        this.applicationStatus = ApplicationStatus.ACCEPTED;
        this.status = ApplicationStatus.ACCEPTED;
        this.decidedAt = LocalDateTime.now();
    }

    public void reject() {
        this.applicationStatus = ApplicationStatus.REJECTED;
        this.status = ApplicationStatus.REJECTED;
        this.decidedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.applicationStatus == ApplicationStatus.PENDING || this.status == ApplicationStatus.PENDING;
    }

    // 호환성 메서드들
    public User getApplicant() {
        return this.applicant;
    }

    public String getMessage() {
        return this.message != null ? this.message : this.applicationText;
    }

    public ApplicationStatus getStatus() {
        return this.status != null ? this.status : this.applicationStatus;
    }

    public Long getPostId() {
        return this.post != null ? this.post.getId() : null;
    }

    public Long getApplicantId() {
        return this.applicant != null ? this.applicant.getId() : null;
    }
}
