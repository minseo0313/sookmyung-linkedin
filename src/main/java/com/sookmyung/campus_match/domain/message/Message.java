package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 1:1 메시지의 단건 레코드
 * - 스레드(MessageThread) 소속
 * - 작성자(sender) 기준 단방향 연관
 * - 소프트 삭제 지원
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_messages_thread_id", columnList = "thread_id"),
                @Index(name = "idx_messages_sender_id", columnList = "sender_id"),
                @Index(name = "idx_messages_created", columnList = "created_at")
        }
)
public class Message extends BaseEntity {

    /** 소속 스레드 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "thread_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_messages_thread"))
    private MessageThread thread;

    /** 발신자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_messages_sender"))
    private User sender;

    /** 본문 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** 소프트 삭제 (true면 화면 노출 제한/대체 문구 처리) */
    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setThread(MessageThread thread) {
        this.thread = thread;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    /* ==========================
       도메인 메서드
       ========================== */

    /** 내용 수정 */
    public void edit(String newContent) {
        if (newContent != null && !newContent.isBlank()) {
            this.content = newContent;
        }
    }

    /** 소프트 삭제 */
    public void softDelete() {
        this.deleted = true;
        // 필요 시 마스킹:
        // this.content = "[삭제된 메시지입니다]";
    }
}
