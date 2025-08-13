package com.sookmyung.campus_match.domain.admin;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 시스템 공지 엔티티
 * - 관리자(Admin)가 등록하는 전체 공지사항
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "system_notices")
public class SystemNotice extends BaseEntity {

    /** 작성자 (관리자) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_system_notices_admin"))
    private Admin admin;

    @Column(nullable = false, length = 200)
    private String title; // 공지 제목

    @Lob
    @Column(nullable = false)
    private String content; // 공지 내용

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true; // 공지 활성 여부 (삭제 대신 비활성 처리 가능)

    /**
     * 작성자 이름 조회
     */
    public String getAdminName() {
        return admin != null ? admin.getName() : null;
    }

    /**
     * 작성자 계정명 조회
     */
    public String getAdminUsername() {
        return admin != null ? admin.getUsername() : null;
    }
}
