package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "게시글 상세 정보 응답")
public class PostDetailResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "프로젝트 팀원 모집합니다")
    private String title;

    @Schema(description = "카테고리", example = "PROJECT")
    private PostCategory category;

    @Schema(description = "내용", example = "웹 개발 프로젝트 팀원을 모집합니다...")
    private String content;

    @Schema(description = "필요 역할", example = "프론트엔드 개발자 2명, 백엔드 개발자 1명")
    private String requiredRoles;

    @Schema(description = "모집 인원", example = "3")
    private Integer recruitmentCount;

    @Schema(description = "기간", example = "3개월")
    private String duration;

    @Schema(description = "링크 URL", example = "https://github.com/project")
    private String linkUrl;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "마감 여부", example = "false")
    private Boolean isClosed;

    @Schema(description = "조회수", example = "100")
    private Integer viewCount;

    @Schema(description = "좋아요 수", example = "15")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "8")
    private Integer commentCount;

    @Schema(description = "작성자 정보")
    private UserResponse author;

    @Schema(description = "생성 시간", example = "2025-08-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2025-08-15T10:30:00")
    private LocalDateTime updatedAt;

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent())
                .requiredRoles(post.getRequiredRoles())
                .recruitmentCount(post.getRecruitmentCount())
                .duration(post.getDuration())
                .linkUrl(post.getLinkUrl())
                .imageUrl(post.getImageUrl())
                .isClosed(post.getIsClosed())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .author(post.getAuthor() != null ? UserResponse.from(post.getAuthor()) : null)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
