package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.message.enum_.ReportStatus;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * 개인 메시지 신고 엔티티
 * - reporter(신고자) → offender(피신고자)의 message를 신고
 * - 상태: PENDING | HANDLED
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "message_reports",
        indexes = {
                @Index(name = "idx_message_reports_message_id", columnList = "message_id"),
                @Index(name = "idx_message_reports_reporter_id", columnList = "reporter_id"),
                @Index(name = "idx_message_reports_offender_id", columnList = "offender_id"),
                @Index(name = "idx_message_reports_status", columnList = "status"),
                @Index(name = "idx_message_reports_created", columnList = "created_at")
        }
)
public class MessageReport extends BaseEntity {

    /** 대상 메시지 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_reports_message"))
    private Message message;

    /** 신고자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_reports_reporter"))
    private User reporter;

    /** 피신고자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_reports_offender"))
    private User offender;

    /** 신고 사유 (자유기술식) */
    @Lob
    @Column(nullable = false)
    private String reason;

    /** 처리 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    /** 처리 시각(선택) */
    @Column
    private Instant handledAt;

    /** 관리자 메모(선택) */
    @Column(length = 255)
    private String adminNote;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public void setOffender(User offender) {
        this.offender = offender;
    }

    /* ==========================
       도메인 메서드
       ========================== */

    public void handle(String adminNote) {
        this.status = ReportStatus.HANDLED;
        this.handledAt = Instant.now();
        this.adminNote = adminNote;
    }

    public void updateReason(String reason) {
        if (reason != null && !reason.isBlank()) {
            this.reason = reason;
        }
    }

    public void changeStatus(ReportStatus status, String adminNote) {
        if (status != null) {
            this.status = status;
            this.handledAt = (status == ReportStatus.HANDLED) ? Instant.now() : null;
            this.adminNote = adminNote;
        }
    }
}
