package com.sookmyung.campus_match.domain.admin;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 시스템 공지 엔티티
 * - 관리자(Admin)가 등록하는 전체 공지사항
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "system_notices")
public class SystemNotice extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title; // 공지 제목

    @Lob
    @Column(nullable = false)
    private String content; // 공지 내용

    @Column(nullable = false)
    private boolean isActive = true; // 공지 활성 여부 (삭제 대신 비활성 처리 가능)
}
