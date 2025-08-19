package com.sookmyung.campus_match.domain.admin;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemNotice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Admin createdBy;

    @Column(name = "notice_title", nullable = false, length = 255)
    private String noticeTitle;

    @Lob
    @Column(name = "notice_content", columnDefinition = "TEXT")
    private String noticeContent;

    @Column(name = "visible_from")
    private LocalDateTime visibleFrom;

    @Column(name = "visible_to")
    private LocalDateTime visibleTo;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "admin_id")
    private Long adminId;

    // 호환성 메서드들
    public String getTitle() {
        return this.title != null ? this.title : this.noticeTitle;
    }

    public String getContent() {
        return this.content != null ? this.content : this.noticeContent;
    }

    public boolean isActive() {
        return this.isActive != null ? this.isActive : true;
    }

    public String getAdminName() {
        return this.createdBy != null && this.createdBy.getUser() != null ? this.createdBy.getUser().getName() : null;
    }

    public String getAdminUsername() {
        return this.createdBy != null && this.createdBy.getUser() != null ? this.createdBy.getUser().getName() : null;
    }
}
