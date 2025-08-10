package com.sookmyung.campus_match.domain.post;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_likes_user_post", columnNames = {"user_id", "post_id"})
        },
        indexes = {
                @Index(name = "idx_post_likes_post_id", columnList = "post_id"),
                @Index(name = "idx_post_likes_user_id", columnList = "user_id"),
                @Index(name = "idx_post_likes_created", columnList = "created_at")
        }
)
public class PostLike extends BaseEntity {

    /** 좋아요를 누른 게시글 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_likes_post"))
    private Post post;

    /** 좋아요를 누른 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_likes_user"))
    private User user;

    /* ==========================
       연관관계 편의 메서드
       ========================== */
    public void setPost(Post post) {
        this.post = post;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
