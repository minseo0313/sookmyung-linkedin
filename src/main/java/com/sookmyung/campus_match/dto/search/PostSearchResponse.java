package com.sookmyung.campus_match.dto.search;

import com.sookmyung.campus_match.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 게시글 검색 결과 응답 DTO.
 * - 목록/검색 시 제목만 노출
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostSearchResponse {

    @Schema(description = "게시글 ID", example = "10")
    private Long id;

    @Schema(description = "제목", example = "백엔드 개발자 모집")
    private String title;

    public static PostSearchResponse from(Post post) {
        return PostSearchResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .build();
    }
}
