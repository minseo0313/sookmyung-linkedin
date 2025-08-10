package com.sookmyung.campus_match.domain.post;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 카테고리 마스터
 * - 예: 개발, 디자인, 공모전, 데이터 분석 등
 * - 관리자/운영진이 추가·수정 가능
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "post_categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_categories_name", columnNames = {"name"})
        }
)
public class PostCategory extends BaseEntity {

    /** 카테고리명 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 카테고리 설명(선택) */
    @Column(length = 255)
    private String description;

    /** 사용 여부 (비활성화된 카테고리는 선택지에서 제외) */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** (옵션) 해당 카테고리에 속한 게시글 목록 */
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    /* ==========================
       상태 변경 메서드
       ========================== */

    public void rename(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
