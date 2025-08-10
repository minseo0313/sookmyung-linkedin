package com.sookmyung.campus_match.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 게시글 생성 요청 DTO.
 * - 필수: 제목, 카테고리 ID, 내용, 모집 인원, 요구 역할, 기간
 * - 선택: 링크, 이미지 URL
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "백엔드 개발자 모집")
    @NotBlank
    @Size(max = 200)
    private String title;

    @Schema(description = "카테고리 ID", example = "3")
    @NotNull
    private Long categoryId;

    @Schema(description = "게시글 내용", example = "Spring Boot 기반 프로젝트에 함께할 개발자를 찾습니다.")
    @NotBlank
    @Size(max = 8000)
    private String content;

    @Schema(description = "모집 인원 수", example = "3")
    @NotNull
    private Integer recruitCount;

    @Schema(description = "요구 역할 목록", example = "[\"백엔드\", \"프론트엔드\"]")
    @NotNull
    private List<@NotBlank @Size(max = 60) String> requiredRoles;

    @Schema(description = "기간", example = "2주")
    @Size(max = 60)
    private String duration;

    @Schema(description = "외부 링크", example = "https://example.com")
    @Size(max = 500)
    private String link;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    @Size(max = 500)
    private String imageUrl;
}
