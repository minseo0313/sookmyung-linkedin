package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_profiles_user_id", columnNames = {"user_id"})
        },
        indexes = {
                @Index(name = "idx_profiles_user_id", columnList = "user_id")
        }
)
public class Profile extends BaseEntity {

    /** 프로필 소유자 (1:1) */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_profiles_user"))
    private User user;

    /** 상단 한 줄 소개 */
    @Column(nullable = false, length = 120)
    private String headline;

    /** 자기소개 본문 */
    @Lob
    @Column(nullable = false)
    private String bio;

    /** 인사 받기 허용 여부 (프로필에서 DM 시작 가능 여부) */
    @Column(nullable = false)
    @Builder.Default
    private boolean greetingEnabled = true;

    /** 경력/활동 목록 (프로필 소유, 삭제 시 함께 제거) */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void addExperience(Experience experience) {
        if (experience == null) return;
        experiences.add(experience);
        experience.setProfile(this);
    }

    public void removeExperience(Experience experience) {
        if (experience == null) return;
        experiences.remove(experience);
        experience.setProfile(null);
    }

    /* ==========================
       상태 변경 메서드
       ========================== */

    public void updateIntro(String headline, String bio) {
        if (headline != null) this.headline = headline;
        if (bio != null) this.bio = bio;
    }

    public void setGreetingEnabled(boolean enabled) {
        this.greetingEnabled = enabled;
    }

    /**
     * 사용자 ID 조회
     */
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}
