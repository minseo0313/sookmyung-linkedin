package com.sookmyung.campus_match.dto.profile;

import com.sookmyung.campus_match.domain.user.enum_.ExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 자기소개(프로필) 수정 요청 DTO.
 * - 관심사, 경력, 활동을 수정 가능
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    @Schema(description = "프로필 한 줄 소개", example = "도전하는 백엔드 개발자입니다.")
    @Size(max = 120)
    private String headline;

    @Schema(description = "자기소개 본문", example = "Java, Spring Boot 기반 프로젝트 경험 보유.")
    @Size(max = 4000)
    private String bio;

    @Schema(description = "인사 기능 활성화 여부", example = "true")
    private Boolean greetingEnabled;

    @Schema(description = "관심사 ID 목록", example = "[1, 3, 5]")
    private List<Long> interestIds;

    @Schema(description = "경력 목록", example = "[{\"type\":\"ACTIVITY\",\"title\":\"환경 데이터 분석 프로젝트\",\"sortOrder\":1}]")
    private List<ExperienceUpdateDto> experiences;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ExperienceUpdateDto {

        @Schema(description = "경력 유형", example = "ACTIVITY")
        @NotNull
        private ExperienceType type;

        @Schema(description = "제목", example = "환경 데이터 분석 프로젝트")
        @NotBlank
        @Size(max = 120)
        private String title;

        @Schema(description = "내용", example = "환경 데이터를 수집하고 분석하여 보고서 작성")
        @Size(max = 2000)
        private String content;

        @Schema(description = "정렬 순서", example = "1")
        private int sortOrder;
    }
}
