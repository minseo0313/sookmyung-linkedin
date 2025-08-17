package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "게시글 작성 요청")
public class PostCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이하여야 합니다")
    @Schema(description = "제목", example = "프로젝트 팀원 모집합니다")
    private String title;

    @NotNull(message = "카테고리는 필수입니다")
    @Schema(description = "카테고리", example = "PROJECT")
    private PostCategory category;

    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 10, max = 5000, message = "내용은 10자 이상 5000자 이하여야 합니다")
    @Schema(description = "내용", example = "웹 개발 프로젝트 팀원을 모집합니다...")
    private String content;

    @Size(max = 200, message = "필요 역할은 200자 이하여야 합니다")
    @Schema(description = "필요 역할", example = "프론트엔드 개발자 2명, 백엔드 개발자 1명")
    private String requiredRoles;

    @Min(value = 1, message = "모집 인원은 1명 이상이어야 합니다")
    @Max(value = 20, message = "모집 인원은 20명 이하여야 합니다")
    @Schema(description = "모집 인원", example = "3")
    private Integer recruitmentCount;

    @Size(max = 100, message = "기간은 100자 이하여야 합니다")
    @Schema(description = "기간", example = "3개월")
    private String duration;

    @Size(max = 500, message = "링크 URL은 500자 이하여야 합니다")
    @Schema(description = "링크 URL", example = "https://github.com/project")
    private String linkUrl;
}
