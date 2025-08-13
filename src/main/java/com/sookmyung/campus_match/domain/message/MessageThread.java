package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.message.enum_.StartedFromType;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 1:1 개인 메시지 스레드(대화방)
 * - 참가자 2명 고정
 * - 시작 출처: 프로필(인사) / 게시글(문의) 구분
 * - 미확인 개수, 마지막 메시지 정보 캐싱
 *
 * 설계 메모
 * - 한 쌍의 사용자 간 스레드는 1개만 유지(서비스 레이어에서 (minId, maxId) 정규화 권장)
 * - startedFromType/Id는 최초 생성 정보 기록(통계/라벨링 용도)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "message_threads",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_message_threads_participants",
                        columnNames = {"participant_a_id", "participant_b_id"})
        },
        indexes = {
                @Index(name = "idx_message_threads_participant_a", columnList = "participant_a_id"),
                @Index(name = "idx_message_threads_participant_b", columnList = "participant_b_id"),
                @Index(name = "idx_message_threads_last_at", columnList = "last_message_at"),
                @Index(name = "idx_message_threads_started_from", columnList = "started_from_type, started_from_id")
        }
)
public class MessageThread extends BaseEntity {

    /** 참가자 A (서비스 레이어에서 항상 더 작은 userId가 A가 되도록 정규화 권장) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_a_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_threads_participant_a"))
    private User participantA;

    /** 참가자 B */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_b_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_threads_participant_b"))
    private User participantB;

    /** 스레드 시작 출처 유형 (PROFILE | POST) */
    @Enumerated(EnumType.STRING)
    @Column(name = "started_from_type", nullable = false, length = 20)
    private StartedFromType startedFromType;

    /** 스레드 시작 출처의 식별자 (예: PROFILE → targetUserId, POST → postId) */
    @Column(name = "started_from_id", nullable = false)
    private Long startedFromId;

    /** 마지막 메시지 시각 (정렬/리스트용) */
    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    /** 마지막 메시지 미리보기 (리스트용) */
    @Column(name = "last_message_preview", length = 200)
    private String lastMessagePreview;

    /** A 사용자의 미확인 메시지 개수 */
    @Column(name = "unread_count_a", nullable = false)
    @Builder.Default
    private int unreadCountA = 0;

    /** B 사용자의 미확인 메시지 개수 */
    @Column(name = "unread_count_b", nullable = false)
    @Builder.Default
    private int unreadCountB = 0;

    /** (옵션) 메시지 목록. 대화방 단위로 삭제 시 메시지도 함께 제거 */
    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    /* ==========================
       연관관계 편의 메서드
       ========================== */
    public void addMessage(Message message) {
        if (message == null) return;
        messages.add(message);
        message.setThread(this);
    }

    /* ==========================
       도메인 메서드
       ========================== */

    /** 마지막 메시지 정보 반영 + 상대방 미확인 수 증가 */
    public void touchLastMessage(String preview, Long senderUserId) {
        this.lastMessageAt = Instant.now();
        this.lastMessagePreview = (preview != null && preview.length() > 200)
                ? preview.substring(0, 200)
                : preview;

        if (senderUserId == null) return;
        if (isParticipantA(senderUserId)) {
            // A가 보냈다면 B의 미확인 증가
            this.unreadCountB = Math.max(0, this.unreadCountB + 1);
        } else if (isParticipantB(senderUserId)) {
            // B가 보냈다면 A의 미확인 증가
            this.unreadCountA = Math.max(0, this.unreadCountA + 1);
        }
    }

    /** 특정 사용자가 스레드를 읽음 처리 */
    public void markReadBy(Long readerUserId) {
        if (readerUserId == null) return;
        if (isParticipantA(readerUserId)) {
            this.unreadCountA = 0;
        } else if (isParticipantB(readerUserId)) {
            this.unreadCountB = 0;
        }
    }

    public boolean isParticipant(Long userId) {
        return isParticipantA(userId) || isParticipantB(userId);
    }

    public boolean isParticipantA(Long userId) {
        return userId != null && participantA != null && participantA.getId().equals(userId);
    }

    public boolean isParticipantB(Long userId) {
        return userId != null && participantB != null && participantB.getId().equals(userId);
    }

    /**
     * 참가자 A ID 조회
     */
    public Long getParticipantAId() {
        return participantA != null ? participantA.getId() : null;
    }

    /**
     * 참가자 B ID 조회
     */
    public Long getParticipantBId() {
        return participantB != null ? participantB.getId() : null;
    }

    /**
     * 마지막 메시지 시간 업데이트 (MessageService에서 사용)
     */
    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
