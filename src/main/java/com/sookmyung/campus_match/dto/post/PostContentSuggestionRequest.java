package com.sookmyung.campus_match.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * AI 게시글 작성 도움 요청 DTO
 * - 카테고리, 요구 역할, 기간, 모집 인원을 기반으로 문장 구조 추천
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostContentSuggestionRequest {

    @Schema(description = "게시글 카테고리", example = "개발")
    private String category;

    @Schema(description = "요구 역할 목록", example = "[\"백엔드\", \"프론트엔드\"]")
    private List<String> requiredRoles;

    @Schema(description = "프로젝트 기간", example = "2주")
    private String duration;

    @Schema(description = "모집 인원 수", example = "3")
    private Integer recruitCount;
}
