package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.StartedFromType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "message_threads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageThread extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private com.sookmyung.campus_match.domain.user.User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private com.sookmyung.campus_match.domain.user.User user2;

    @Enumerated(EnumType.STRING)
    @Column(name = "started_from_type", nullable = false)
    private StartedFromType startedFromType;

    @Column(name = "started_from_id", nullable = false)
    private Long startedFromId;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "participant_a_id")
    private Long participantAId;

    @Column(name = "participant_b_id")
    private Long participantBId;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "last_message_preview", length = 200)
    private String lastMessagePreview;

    @Column(name = "unread_count_a")
    private Integer unreadCountA;

    @Column(name = "unread_count_b")
    private Integer unreadCountB;

    // 도메인 메서드들
    public void touchUpdatedAt() {
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = LocalDateTime.ofInstant(lastMessageAt, ZoneId.systemDefault());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        this.setUpdatedAt(LocalDateTime.now());
    }

    // 호환성 메서드들
    public com.sookmyung.campus_match.domain.user.User getParticipantA() {
        return this.user1;
    }

    public com.sookmyung.campus_match.domain.user.User getParticipantB() {
        return this.user2;
    }

    public Long getParticipantAId() {
        return this.participantAId != null ? this.participantAId : (this.user1 != null ? this.user1.getId() : null);
    }

    public Long getParticipantBId() {
        return this.participantBId != null ? this.participantBId : (this.user2 != null ? this.user2.getId() : null);
    }

    public LocalDateTime getLastMessageAt() {
        return this.lastMessageAt;
    }

    public String getLastMessagePreview() {
        return this.lastMessagePreview;
    }

    public int getUnreadCountA() {
        return this.unreadCountA != null ? this.unreadCountA : 0;
    }

    public int getUnreadCountB() {
        return this.unreadCountB != null ? this.unreadCountB : 0;
    }
}
