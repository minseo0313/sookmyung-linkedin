package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.ExperienceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_type", nullable = false)
    private ExperienceType experienceType;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "type")
    private String type;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // 호환성 메서드들
    public String getType() {
        return this.type != null ? this.type : (this.experienceType != null ? this.experienceType.name() : null);
    }

    public String getContent() {
        return this.content != null ? this.content : this.description;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }
}
