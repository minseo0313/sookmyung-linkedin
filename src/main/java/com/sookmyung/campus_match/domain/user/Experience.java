package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.enum_.ExperienceType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "experiences",
        indexes = {
                @Index(name = "idx_experiences_profile_id", columnList = "profile_id"),
                @Index(name = "idx_experiences_type", columnList = "type"),
                @Index(name = "idx_experiences_sort_order", columnList = "sortOrder")
        }
)
public class Experience extends BaseEntity {

    /** 소속 프로필 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_experiences_profile"))
    private Profile profile;

    /** CAREER(경력) | ACTIVITY(활동) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExperienceType type;

    /**
     * 제목
     * - CAREER: 한 줄 요약(예: 동아리 임원)
     * - ACTIVITY: 활동 제목(예: 환경 데이터 분석 프로젝트)
     */
    @Column(nullable = false, length = 120)
    private String title;

    /**
     * 상세 내용 (활동 위주로 사용, 경력은 보통 비워둠)
     */
    @Lob
    @Column
    private String content;

    /**
     * 표시 순서(내림/오름 정렬 용도)
     */
    @Column(nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    /* ==========================
       연관관계/상태 변경 메서드
       ========================== */

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void changeType(ExperienceType type) {
        if (type != null) this.type = type;
    }

    public void updateTitle(String title) {
        if (title != null) this.title = title;
    }

    public void updateContent(String content) {
        this.content = content; // null 허용 -> 내용 제거 가능
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
