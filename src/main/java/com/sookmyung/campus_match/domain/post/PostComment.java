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
        name = "post_comments",
        indexes = {
                @Index(name = "idx_post_comments_post_id", columnList = "post_id"),
                @Index(name = "idx_post_comments_author_id", columnList = "author_id"),
                @Index(name = "idx_post_comments_created", columnList = "created_at")
        }
)
public class PostComment extends BaseEntity {

    /** 대상 게시글 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_comments_post"))
    private Post post;

    /** 작성자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_comments_user"))
    private User author;

    /** 댓글 본문 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** 소프트 삭제 플래그 (true면 화면 노출 제한/대체 문구 처리) */
    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    // 필요 시 대댓글(계층) 지원:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_post_comments_parent"))
    // private PostComment parent;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setPost(Post post) {
        this.post = post;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    /* ==========================
       상태 변경 메서드
       ========================== */

    /** 내용 수정 */
    public void edit(String newContent) {
        if (newContent != null && !newContent.isBlank()) {
            this.content = newContent;
        }
    }

    /** 소프트 삭제 */
    public void softDelete() {
        this.deleted = true;
        // 필요 시 content 마스킹 로직 추가 가능
        // this.content = "[삭제된 댓글입니다]";
    }
}
