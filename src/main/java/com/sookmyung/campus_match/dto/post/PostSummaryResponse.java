package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 목록/요약 조회용 DTO.
 * - 상세 내용(content) 제외
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostSummaryResponse {

    @Schema(description = "게시글 ID", example = "10")
    private Long id;

    @Schema(description = "작성자 ID", example = "5")
    private Long authorId;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;

    @Schema(description = "제목", example = "백엔드 개발자 모집")
    private String title;

    @Schema(description = "모집 인원", example = "3")
    private int recruitCount;

    @Schema(description = "조회수", example = "120")
    private long views;

    @Schema(description = "좋아요 수", example = "15")
    private long likeCount;

    @Schema(description = "댓글 수", example = "7")
    private long commentCount;

    @Schema(description = "마감 여부", example = "false")
    private boolean closed;

    @Schema(description = "작성일시", example = "2025-08-10T14:30:00")
    private LocalDateTime createdAt;

    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .categoryId(post.getCategoryId())
                .title(post.getTitle())
                .recruitCount(post.getRecruitCount())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .closed(post.isClosed())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
