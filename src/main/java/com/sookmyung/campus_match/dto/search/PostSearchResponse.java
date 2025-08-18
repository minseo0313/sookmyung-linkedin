package com.sookmyung.campus_match.dto.search;

import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 검색 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostSearchResponse {

    private Long id;
    private PostCategory category;
    private String title;
    private String content;
    private Integer recruitmentCount;
    private String duration;
    private Boolean isClosed;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Long authorId;
    private String authorName;
    private String authorDepartment;
    private LocalDateTime createdAt;

    public static PostSearchResponse from(com.sookmyung.campus_match.domain.post.Post post) {
        return PostSearchResponse.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .recruitmentCount(post.getRecruitmentCount())
                .duration(post.getDuration())
                .isClosed(post.isClosed())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthor() != null ? post.getAuthor().getName() : null)
                .authorDepartment(post.getAuthor() != null ? post.getAuthor().getDepartment() : null)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
