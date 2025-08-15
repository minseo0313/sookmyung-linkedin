package com.sookmyung.campus_match.domain.message;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Lob
    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private MessageThread thread;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "deleted")
    private Boolean deleted;

    // 도메인 메서드들
    public void edit(String newContent) {
        if (newContent != null && !newContent.isBlank()) {
            this.messageContent = newContent;
            this.content = newContent;
        }
    }

    public void softDelete() {
        this.deleted = true;
    }

    // 호환성 메서드들
    public String getContent() {
        return this.content != null ? this.content : this.messageContent;
    }

    public boolean isDeleted() {
        return this.deleted != null ? this.deleted : false;
    }

    public Long getThreadId() {
        return this.thread != null ? this.thread.getId() : null;
    }

    public Long getSenderId() {
        return this.sender != null ? this.sender.getId() : null;
    }
}
