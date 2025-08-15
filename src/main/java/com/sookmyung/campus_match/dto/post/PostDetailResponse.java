package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 응답 DTO.
 * - 본문(content)과 모든 주요 정보 포함
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostDetailResponse {

    @Schema(description = "게시글 ID", example = "10")
    private Long id;

    @Schema(description = "작성자 ID", example = "5")
    private Long authorId;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;

    @Schema(description = "제목", example = "백엔드 개발자 모집")
    private String title;

    @Schema(description = "본문 내용", example = "Spring Boot 기반 프로젝트에 함께할 개발자를 찾습니다.")
    private String content;

    @Schema(description = "모집 인원", example = "3")
    private int recruitCount;

    @Schema(description = "요구 역할 목록", example = "[\"백엔드\", \"프론트엔드\"]")
    private List<String> requiredRoles;

    @Schema(description = "기간", example = "2주")
    private String duration;

    @Schema(description = "외부 링크", example = "https://example.com")
    private String link;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "조회수", example = "120")
    private long views;

    @Schema(description = "좋아요 수", example = "15")
    private long likeCount;

    @Schema(description = "댓글 수", example = "7")
    private long commentCount;

    @Schema(description = "마감 여부", example = "false")
    private boolean closed;

    @Schema(description = "매칭된 팀 ID", example = "2")
    private Long matchedTeamId;

    @Schema(description = "작성일시", example = "2025-08-10T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-08-11T09:15:00")
    private LocalDateTime updatedAt;

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .categoryId(post.getCategoryId())
                .title(post.getTitle())
                .content(post.getContent())
                .recruitCount(post.getRecruitCount())
                .requiredRoles(post.getRequiredRoles() != null ? 
                        List.of(post.getRequiredRoles().split(",")) : List.of())
                .duration(post.getDuration())
                .link(post.getLink())
                .imageUrl(post.getImageUrl())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .closed(post.isClosed())
                .matchedTeamId(post.getMatchedTeamId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
