package com.sookmyung.campus_match.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sookmyung.campus_match.domain.post.PostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 응답 DTO.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostCommentResponse {

    @Schema(description = "댓글 ID", example = "15")
    private Long id;

    @Schema(description = "게시글 ID", example = "10")
    private Long postId;

    @Schema(description = "작성자 ID", example = "5")
    private Long userId;

    @Schema(description = "댓글 내용", example = "저도 참여하고 싶습니다!")
    private String commentContent;

    @Schema(description = "삭제 여부", example = "false")
    private boolean deleted;

    @Schema(description = "작성일시", example = "2025-08-10T14:35:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static PostCommentResponse from(PostComment comment) {
        return PostCommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .commentContent(comment.getCommentContent())
                .deleted(comment.isDeleted())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
