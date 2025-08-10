package com.sookmyung.campus_match.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 게시글 좋아요 수 응답 DTO.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostLikeCountResponse {

    @Schema(description = "게시글 ID", example = "10")
    private Long postId;

    @Schema(description = "좋아요 개수", example = "25")
    private long likeCount;

    public static PostLikeCountResponse of(Long postId, long likeCount) {
        return PostLikeCountResponse.builder()
                .postId(postId)
                .likeCount(likeCount)
                .build();
    }
}
