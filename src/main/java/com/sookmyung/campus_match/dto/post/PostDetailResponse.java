package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 상세 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostDetailResponse {

    private Long id;
    private PostCategory category;
    private String title;
    private String content;
    private String requiredRoles;
    private Integer recruitmentCount;
    private String duration;
    private String linkUrl;
    private String imageUrl;
    private Boolean isClosed;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Long authorId;
    private String authorName;
    private String authorDepartment;
    private String authorStudentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDetailResponse from(com.sookmyung.campus_match.domain.post.Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .requiredRoles(post.getRequiredRoles())
                .recruitmentCount(post.getRecruitmentCount())
                .duration(post.getDuration())
                .linkUrl(post.getLinkUrl())
                .imageUrl(post.getImageUrl())
                .isClosed(post.isClosed())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthor() != null ? post.getAuthor().getName() : null)
                .authorDepartment(post.getAuthor() != null ? post.getAuthor().getDepartment() : null)
                .authorStudentId(post.getAuthor() != null ? post.getAuthor().getStudentId() : null)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
