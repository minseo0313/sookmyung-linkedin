package com.sookmyung.campus_match.dto.profile;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 프로필 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long userId;
    private String department;
    private String studentCode;
    private String headline;
    private String bio;
    private String profileImageUrl;
    private String location;
    private String websiteUrl;
    private String linkedinUrl;
    private Integer viewCount;
    private Boolean greetingEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProfileResponse from(com.sookmyung.campus_match.domain.user.Profile profile, java.util.List<Object> interests, java.util.List<Object> experiences) {
        return ProfileResponse.builder()
                .userId(profile.getUserId())
                .department(profile.getUser() != null ? profile.getUser().getDepartment() : null)
                .studentCode(profile.getUser() != null ? profile.getUser().getStudentId() : null)
                .headline(profile.getHeadline())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .location(profile.getLocation())
                .websiteUrl(profile.getWebsiteUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .viewCount(profile.getViewCount())
                .greetingEnabled(Boolean.valueOf(profile.isGreetingEnabled()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
