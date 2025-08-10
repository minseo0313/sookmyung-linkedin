package com.sookmyung.campus_match.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 게시글 수정 요청 DTO.
 * - 모든 필드는 선택, null이 아닌 값만 수정
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    @Schema(description = "게시글 제목", example = "백엔드 개발자 모집 - 수정")
    @Size(max = 200)
    private String title;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;

    @Schema(description = "게시글 내용", example = "Spring Boot 프로젝트 경험자 우대.")
    @Size(max = 8000)
    private String content;

    @Schema(description = "모집 인원 수", example = "4")
    private Integer recruitCount;

    @Schema(description = "요구 역할 목록", example = "[\"백엔드\", \"프론트엔드\"]")
    private List<@Size(max = 60) String> requiredRoles;

    @Schema(description = "기간", example = "3주")
    @Size(max = 60)
    private String duration;

    @Schema(description = "외부 링크", example = "https://example.com")
    @Size(max = 500)
    private String link;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    @Size(max = 500)
    private String imageUrl;
}
