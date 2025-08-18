package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.StartedFromType;
import com.sookmyung.campus_match.domain.user.User;
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
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

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

    // 새로운 필드들 (서비스 코드 호환성을 위해)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant1_id")
    private User participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant2_id")
    private User participant2;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    // 도메인 메서드들
    public void touch() {
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void touchUpdatedAt() {
        this.touch();
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = LocalDateTime.ofInstant(lastMessageAt, ZoneId.systemDefault());
        this.touch();
    }

    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        this.lastMessageTime = lastMessageAt;
        this.touch();
    }

    public void updateLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
        this.lastMessageAt = lastMessageTime;
        this.touch();
    }

    public boolean isParticipant(Long userId) {
        return (user1 != null && user1.getId().equals(userId)) ||
               (user2 != null && user2.getId().equals(userId)) ||
               (participant1 != null && participant1.getId().equals(userId)) ||
               (participant2 != null && participant2.getId().equals(userId));
    }

    // 호환성 메서드들
    public User getParticipantA() {
        return this.user1 != null ? this.user1 : this.participant1;
    }

    public User getParticipantB() {
        return this.user2 != null ? this.user2 : this.participant2;
    }

    public Long getParticipantAId() {
        return this.participantAId != null ? this.participantAId : 
               (this.user1 != null ? this.user1.getId() : 
               (this.participant1 != null ? this.participant1.getId() : null));
    }

    public Long getParticipantBId() {
        return this.participantBId != null ? this.participantBId : 
               (this.user2 != null ? this.user2.getId() : 
               (this.participant2 != null ? this.participant2.getId() : null));
    }

    public LocalDateTime getLastMessageAt() {
        return this.lastMessageAt != null ? this.lastMessageAt : this.lastMessageTime;
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
