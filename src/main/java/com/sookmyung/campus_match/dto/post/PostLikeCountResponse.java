package com.sookmyung.campus_match.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 게시글 좋아요 수 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeCountResponse {
    
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;
    
    @Schema(description = "좋아요 수", example = "25")
    private Long likeCount;
    
    public static PostLikeCountResponse of(Long postId, Long likeCount) {
        return PostLikeCountResponse.builder()
                .postId(postId)
                .likeCount(likeCount)
                .build();
    }
}




