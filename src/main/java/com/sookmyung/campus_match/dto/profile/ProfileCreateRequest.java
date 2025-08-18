package com.sookmyung.campus_match.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 프로필 생성 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileCreateRequest {

    @Size(max = 255, message = "헤드라인은 255자를 초과할 수 없습니다")
    private String headline;

    @Size(max = 100, message = "자기소개는 100자를 초과할 수 없습니다")
    private String bio;

    @Size(max = 255, message = "프로필 이미지 URL은 255자를 초과할 수 없습니다")
    private String profileImageUrl;

    @Size(max = 255, message = "위치는 255자를 초과할 수 없습니다")
    private String location;

    @Size(max = 255, message = "웹사이트 URL은 255자를 초과할 수 없습니다")
    private String websiteUrl;

    @Size(max = 255, message = "LinkedIn URL은 255자를 초과할 수 없습니다")
    private String linkedinUrl;

    private Boolean greetingEnabled;

    public boolean isGreetingEnabled() {
        return greetingEnabled != null ? greetingEnabled : false;
    }
}
