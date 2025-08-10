package com.sookmyung.campus_match.domain.post;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.post.enum_.ApplicationStatus;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * 게시글 참여 신청 엔티티
 * - 한 사용자(User)는 하나의 게시글(Post)에 중복 신청 불가
 * - 상태: PENDING | ACCEPTED | REJECTED
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "post_applications",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_applications_post_applicant",
                        columnNames = {"post_id", "applicant_id"})
        },
        indexes = {
                @Index(name = "idx_post_applications_post_id", columnList = "post_id"),
                @Index(name = "idx_post_applications_applicant_id", columnList = "applicant_id"),
                @Index(name = "idx_post_applications_status", columnList = "status"),
                @Index(name = "idx_post_applications_created", columnList = "created_at")
        }
)
public class PostApplication extends BaseEntity {

    /** 대상 게시글 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_applications_post"))
    private Post post;

    /** 신청자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_applications_user"))
    private User applicant;

    /** 자기소개/지원 메시지(선택) */
    @Lob
    @Column
    private String message;

    /** 신청 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    /** 수락/거절 시각(선택) */
    @Column
    private Instant decidedAt;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setPost(Post post) {
        this.post = post;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    /* ==========================
       도메인 메서드
       ========================== */

    public void accept() {
        this.status = ApplicationStatus.ACCEPTED;
        this.decidedAt = Instant.now();
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
        this.decidedAt = Instant.now();
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    /**
     * 게시글 ID 조회
     */
    public Long getPostId() {
        return post != null ? post.getId() : null;
    }

    /**
     * 신청자 ID 조회
     */
    public Long getApplicantId() {
        return applicant != null ? applicant.getId() : null;
    }
}
