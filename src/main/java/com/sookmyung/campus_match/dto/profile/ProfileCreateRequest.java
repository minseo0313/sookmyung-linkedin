package com.sookmyung.campus_match.dto.profile;

import com.sookmyung.campus_match.domain.common.enums.ExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 자기소개(프로필) 생성 요청 DTO.
 * - 관심사, 경력, 활동 정보를 함께 등록
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileCreateRequest {

    @Schema(description = "프로필 한 줄 소개", example = "함께 성장하는 개발자 지망생입니다.")
    @Size(max = 120)
    private String headline;

    @Schema(description = "자기소개 본문", example = "백엔드와 데이터 분석에 관심이 있습니다. 다양한 프로젝트에 참여하고 싶어요.")
    @Size(max = 4000)
    private String bio;

    @Schema(description = "인사 기능 활성화 여부", example = "true")
    @Builder.Default
    private boolean greetingEnabled = true;

    @Schema(description = "관심사 ID 목록", example = "[1, 3, 5]")
    @NotNull
    private List<Long> interestIds;

    @Schema(description = "경력 목록", example = "[{\"type\":\"CAREER\",\"title\":\"동아리 임원\",\"sortOrder\":1}]")
    private List<ExperienceCreateDto> experiences;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ExperienceCreateDto {

        @Schema(description = "경력 유형", example = "CAREER")
        @NotNull
        private ExperienceType type;

        @Schema(description = "제목", example = "동아리 임원")
        @NotBlank
        @Size(max = 120)
        private String title;

        @Schema(description = "내용", example = "동아리 회장으로서 프로젝트 관리 및 운영을 담당")
        @Size(max = 2000)
        private String content;

        @Schema(description = "정렬 순서", example = "1")
        private int sortOrder;
    }
}
