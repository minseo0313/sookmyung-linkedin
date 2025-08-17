package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import com.sookmyung.campus_match.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "게시글 요약 정보 응답")
public class PostSummaryResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "프로젝트 팀원 모집합니다")
    private String title;

    @Schema(description = "카테고리", example = "PROJECT")
    private PostCategory category;

    @Schema(description = "모집 인원", example = "3")
    private Integer recruitmentCount;

    @Schema(description = "기간", example = "3개월")
    private String duration;

    @Schema(description = "마감 여부", example = "false")
    private Boolean isClosed;

    @Schema(description = "조회수", example = "100")
    private Integer viewCount;

    @Schema(description = "좋아요 수", example = "15")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "8")
    private Integer commentCount;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String authorName;

    @Schema(description = "생성 시간", example = "2025-08-15T10:00:00")
    private LocalDateTime createdAt;

    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .recruitmentCount(post.getRecruitmentCount())
                .duration(post.getDuration())
                .isClosed(post.getIsClosed())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .authorName(post.getAuthor() != null ? post.getAuthor().getName() : null)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
