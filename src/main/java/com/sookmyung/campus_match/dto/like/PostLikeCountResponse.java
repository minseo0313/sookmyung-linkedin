package com.sookmyung.campus_match.dto.like;

import lombok.*;

/**
 * 게시글 좋아요 수 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostLikeCountResponse {

    private Long postId;
    private Integer likeCount;
}
