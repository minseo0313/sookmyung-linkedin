package com.sookmyung.campus_match.domain.post;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
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
        name = "posts",
        indexes = {
                @Index(name = "idx_posts_author_id", columnList = "author_id"),
                @Index(name = "idx_posts_category_id", columnList = "category_id"),
                @Index(name = "idx_posts_closed", columnList = "is_closed"),
                @Index(name = "idx_posts_created", columnList = "created_at")
        }
)
public class Post extends BaseEntity {

    /** 작성자 (필수) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_posts_user"))
    private User author;

    /** 카테고리 (예: 개발/디자인/공모전 등) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "fk_posts_category"))
    private PostCategory category;

    /** 제목 */
    @Column(nullable = false, length = 150)
    private String title;

    /** 본문 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** 모집 인원 */
    @Column(nullable = false)
    @Builder.Default
    private int recruitCount = 1;

    /** 요구 역할(예: "디자이너", "백엔드", "데이터") */
    @ElementCollection
    @CollectionTable(
            name = "post_required_roles",
            joinColumns = @JoinColumn(name = "post_id",
                    foreignKey = @ForeignKey(name = "fk_post_required_roles_post"))
    )
    @Column(name = "role", length = 50, nullable = false)
    @Builder.Default
    private List<String> requiredRoles = new ArrayList<>();

    /** 기간(자유 형식: "2주", "2025-08-15 ~ 2025-09-01" 등) */
    @Column(length = 60)
    private String duration;

    /** 관련 링크(선택) */
    @Column(length = 500)
    private String link;

    /** 대표 이미지(선택) */
    @Column(length = 500)
    private String imageUrl;

    /** 조회수 */
    @Column(nullable = false)
    @Builder.Default
    private long views = 0L;

    /** 좋아요 수 */
    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    /** 댓글 수 */
    @Column(nullable = false)
    @Builder.Default
    private int commentCount = 0;

    /** 모집 마감 여부 (팀 매칭 완료 시 true) */
    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private boolean closed = false;

    /** 매칭된 팀 ID(선택, 생성 후 연결) */
    @Column
    private Long matchedTeamId;

    /* ============= (옵션) 양방향 연관 ============= */
    /** 댓글 목록 */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostComment> comments = new ArrayList<>();

    /** 좋아요 목록 */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    /** 신청 목록 */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostApplication> applications = new ArrayList<>();

    /* ============= 편의/도메인 메서드 ============= */

    /** 조회수 증가 */
    public void increaseViews(long by) {
        if (by > 0) this.views += by;
    }

    /** 좋아요 수 증감(토글 처리 시 서비스에서 호출) */
    public void increaseLikeCount(int by) {
        this.likeCount = Math.max(0, this.likeCount + by);
    }

    /** 댓글 수 증감 */
    public void increaseCommentCount(int by) {
        this.commentCount = Math.max(0, this.commentCount + by);
    }

    /** 모집 마감 처리 */
    public void closeRecruitment() {
        this.closed = true;
    }

    /** 팀 매칭 연결 */
    public void linkMatchedTeam(Long teamId) {
        this.matchedTeamId = teamId;
        this.closed = true;
    }

    /* 연관관계 편의 메서드 (필요 시 사용) */
    public void addComment(PostComment comment) {
        if (comment == null) return;
        comments.add(comment);
        comment.setPost(this);
        increaseCommentCount(1);
    }

    public void removeComment(PostComment comment) {
        if (comment == null) return;
        comments.remove(comment);
        comment.setPost(null);
        increaseCommentCount(-1);
    }

    public void addApplication(PostApplication application) {
        if (application == null) return;
        applications.add(application);
        application.setPost(this);
    }

    public void addRequiredRole(String role) {
        if (role != null && !role.isBlank()) requiredRoles.add(role);
    }
}
