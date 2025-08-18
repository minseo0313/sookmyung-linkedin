package com.sookmyung.campus_match.dto.comment;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostCommentResponse {

    private Long id;
    private String content;
    private Long postId;
    private Long authorId;
    private String authorName;
    private String authorDepartment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostCommentResponse from(com.sookmyung.campus_match.domain.post.PostComment comment) {
        return PostCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getCommentContent())
                .postId(comment.getPostId())
                .authorId(comment.getUserId())
                .authorName(comment.getUser() != null ? comment.getUser().getName() : null)
                .authorDepartment(comment.getUser() != null ? comment.getUser().getDepartment() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
