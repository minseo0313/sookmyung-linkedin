package com.sookmyung.campus_match.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * AI 게시글 작성 도움 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostContentSuggestionResponse {

    @Schema(description = "AI가 추천하는 게시글 내용", example = "【프로젝트 소개】\n안녕하세요! 개발 프로젝트에 함께할 팀원을 모집합니다...")
    private String suggestedContent;

    @Schema(description = "추천 템플릿 유형", example = "개발")
    private String templateType;

    public static PostContentSuggestionResponse of(String suggestedContent, String templateType) {
        return PostContentSuggestionResponse.builder()
                .suggestedContent(suggestedContent)
                .templateType(templateType)
                .build();
    }
}
