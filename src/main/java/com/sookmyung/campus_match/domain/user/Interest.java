package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.enum_.InterestType;
import jakarta.persistence.*;
import lombok.*;

/**
 * 관심사 마스터 테이블
 * - 예: 디자인, 개발, 데이터 분석, 논문, 생명 ...
 * - PREDEFINED: 시스템이 제공하는 기본 관심사
 * - CUSTOM: 사용자가 직접 입력한 관심사
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "interests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_interests_name_type", columnNames = {"name", "type"})
        },
        indexes = {
                @Index(name = "idx_interests_type", columnList = "type")
        }
)
public class Interest extends BaseEntity {

    /** 관심사 이름 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 관심사 유형 (기본/커스텀) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterestType type;

    /**
     * 이름 수정 (CUSTOM일 때만 허용)
     */
    public void rename(String newName) {
        if (newName != null && this.type == InterestType.CUSTOM) {
            this.name = newName;
        }
    }
}
