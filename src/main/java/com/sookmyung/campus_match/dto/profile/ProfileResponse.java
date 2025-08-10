package com.sookmyung.campus_match.dto.profile;

import com.sookmyung.campus_match.domain.user.Experience;
import com.sookmyung.campus_match.domain.user.Profile;
import com.sookmyung.campus_match.domain.user.enum_.ExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 자기소개(프로필) 응답 DTO.
 * - 민감 정보는 제외
 * - 관심사와 경력 목록 포함
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileResponse {

    @Schema(description = "프로필 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "10")
    private Long userId;

    @Schema(description = "프로필 한 줄 소개", example = "함께 성장하는 백엔드 개발자")
    private String headline;

    @Schema(description = "자기소개 본문", example = "Spring Boot 기반 프로젝트 경험 보유")
    private String bio;

    @Schema(description = "인사 기능 활성화 여부", example = "true")
    private boolean greetingEnabled;

    @Schema(description = "관심사 목록", example = "[\"개발\", \"데이터 분석\"]")
    private List<String> interests;

    @Schema(description = "경력/활동 목록")
    private List<ExperienceDto> experiences;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ExperienceDto {

        @Schema(description = "경력 유형", example = "CAREER")
        private ExperienceType type;

        @Schema(description = "제목", example = "동아리 임원")
        private String title;

        @Schema(description = "내용", example = "동아리 회장으로 프로젝트 관리")
        private String content;

        @Schema(description = "정렬 순서", example = "1")
        private int sortOrder;

        public static ExperienceDto from(Experience exp) {
            return ExperienceDto.builder()
                    .type(exp.getType())
                    .title(exp.getTitle())
                    .content(exp.getContent())
                    .sortOrder(exp.getSortOrder())
                    .build();
        }
    }

    public static ProfileResponse from(Profile profile, List<String> interestNames, List<Experience> experiences) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .headline(profile.getHeadline())
                .bio(profile.getBio())
                .greetingEnabled(profile.isGreetingEnabled())
                .interests(interestNames)
                .experiences(experiences.stream()
                        .map(ExperienceDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
