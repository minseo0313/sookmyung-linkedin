package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import com.sookmyung.campus_match.domain.common.enums.MessageReportReason;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_message_id", nullable = false)
    private Message reportedMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Lob
    @Column(name = "report_reason", columnDefinition = "TEXT")
    private String reportReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ApplicationStatus reportStatus;

    // MessageService에서 호출하는 필드들
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private MessageReportReason reason;

    @Column(name = "description")
    private String description;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    // 호환성 메서드들
    public MessageReportReason getReason() {
        return this.reason;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getReportedAt() {
        return this.reportedAt;
    }

    // 빌더 메서드 추가
    public static class MessageReportBuilder {
        public MessageReportBuilder reason(MessageReportReason reason) {
            this.reason = reason;
            return this;
        }

        public MessageReportBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MessageReportBuilder reportedAt(LocalDateTime reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }
    }
}
