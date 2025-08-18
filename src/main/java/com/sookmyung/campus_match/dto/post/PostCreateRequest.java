package com.sookmyung.campus_match.dto.post;

import com.sookmyung.campus_match.domain.common.enums.PostCategory;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 게시글 생성 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    @NotNull(message = "카테고리는 필수입니다")
    private PostCategory category;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Size(max = 10000, message = "내용은 10000자를 초과할 수 없습니다")
    private String content;

    @Size(max = 255, message = "필요 역할은 255자를 초과할 수 없습니다")
    private String requiredRoles;

    @Min(value = 1, message = "모집 인원은 1명 이상이어야 합니다")
    @Max(value = 100, message = "모집 인원은 100명을 초과할 수 없습니다")
    private Integer recruitmentCount;

    @Size(max = 255, message = "기간은 255자를 초과할 수 없습니다")
    private String duration;

    @Size(max = 255, message = "링크 URL은 255자를 초과할 수 없습니다")
    private String linkUrl;

    @Size(max = 255, message = "이미지 URL은 255자를 초과할 수 없습니다")
    private String imageUrl;
}
