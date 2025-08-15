package com.sookmyung.campus_match.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "department", length = 255)
    private String department;

    @Column(name = "student_code", length = 2)
    private String studentCode;

    @Column(name = "bio", length = 100)
    private String bio;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "greeting_enabled")
    private Boolean greetingEnabled;

    // 도메인 메서드들
    public void update(String headline, String bio, boolean greetingEnabled) {
        this.headline = headline;
        this.bio = bio;
        this.greetingEnabled = greetingEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 1 : this.viewCount + 1);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 호환성 메서드들
    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
    }

    public String getHeadline() {
        return this.headline;
    }

    public boolean isGreetingEnabled() {
        return this.greetingEnabled != null ? this.greetingEnabled : false;
    }

    // 빌더 메서드 추가
    public static class ProfileBuilder {
        public ProfileBuilder headline(String headline) {
            this.headline = headline;
            return this;
        }
    }
}
