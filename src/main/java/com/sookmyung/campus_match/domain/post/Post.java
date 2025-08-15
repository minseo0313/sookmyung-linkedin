package com.sookmyung.campus_match.domain.post;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PostCategory category;

    @Column(name = "post_title", nullable = false, length = 255)
    private String postTitle;

    @Lob
    @Column(name = "post_content", columnDefinition = "TEXT")
    private String postContent;

    @Column(name = "required_roles", length = 255)
    private String requiredRoles;

    @Column(name = "recruitment_count")
    private Integer recruitmentCount;

    @Column(name = "duration", length = 255)
    private String duration;

    @Column(name = "link_url", length = 255)
    private String linkUrl;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_closed")
    private Boolean isClosed;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "matched_team_id")
    private Long matchedTeamId;

    // TODO: DB 마이그레이션 시 제거할 필드들
    // @Column(name = "user_id") - author_id와 중복, author_id만 사용

    // 도메인 메서드들
    public void closeRecruit() {
        this.isClosed = true;
    }

    public void closeRecruitment() {
        this.isClosed = true;
    }

    public void openRecruit() {
        this.isClosed = false;
    }

    public void increaseView() {
        this.viewCount = (this.viewCount == null ? 1 : this.viewCount + 1);
    }

    public void increaseLike() {
        this.likeCount = (this.likeCount == null ? 1 : this.likeCount + 1);
    }

    public void decreaseLike() {
        if (this.likeCount != null && this.likeCount > 0) this.likeCount--;
    }

    public void increaseComment() {
        this.commentCount = (this.commentCount == null ? 1 : this.commentCount + 1);
    }

    public void decreaseComment() {
        if (this.commentCount != null && this.commentCount > 0) this.commentCount--;
    }

    public void increaseCommentCount(int by) {
        this.commentCount = (this.commentCount == null ? by : this.commentCount + by);
    }

    public void increaseLikeCount(int by) {
        this.likeCount = (this.likeCount == null ? by : this.likeCount + by);
    }

    public void linkMatchedTeam(Long teamId) {
        this.matchedTeamId = teamId;
        this.isClosed = true;
    }

    // 호환성 메서드들
    public User getAuthor() {
        return this.author;
    }

    public String getTitle() {
        return this.postTitle;
    }

    public String getContent() {
        return this.postContent;
    }

    public Long getViews() {
        return this.viewCount != null ? this.viewCount.longValue() : 0L;
    }

    public boolean isClosed() {
        return this.isClosed != null ? this.isClosed : false;
    }

    public Long getAuthorId() {
        return this.author != null ? this.author.getId() : null;
    }

    public Long getCategoryId() {
        return this.category != null ? this.category.getId() : null;
    }

    public Integer getRecruitCount() {
        return this.recruitmentCount;
    }

    public String getLink() {
        return this.linkUrl;
    }

    // 빌더 메서드 추가
    public static class PostBuilder {
        public PostBuilder recruitCount(Integer recruitCount) {
            this.recruitmentCount = recruitCount;
            return this;
        }

        public PostBuilder link(String link) {
            this.linkUrl = link;
            return this;
        }
    }
}
