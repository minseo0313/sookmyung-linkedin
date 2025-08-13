package com.sookmyung.campus_match.service.profile;

import com.sookmyung.campus_match.domain.user.Profile;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.profile.ProfileCreateRequest;
import com.sookmyung.campus_match.dto.profile.ProfileResponse;
import com.sookmyung.campus_match.dto.profile.ProfileUpdateRequest;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.user.ProfileRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse createProfile(ProfileCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 이미 프로필이 있는지 확인
        if (profileRepository.existsByUser(user)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "이미 프로필이 존재합니다.");
        }

        Profile profile = Profile.builder()
                .user(user)
                .headline(request.getHeadline())
                .bio(request.getBio())
                .greetingEnabled(request.isGreetingEnabled())
                .build();

        Profile savedProfile = profileRepository.save(profile);
        // TODO: 실제 관심사와 경력 데이터를 가져와서 전달해야 함
        return ProfileResponse.from(savedProfile, List.of(), List.of());
    }

    @Transactional
    public ProfileResponse updateProfile(Long profileId, ProfileUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "프로필을 찾을 수 없습니다."));

        // 본인의 프로필만 수정 가능
        if (!profile.getUser().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "본인의 프로필만 수정할 수 있습니다.");
        }

        profile.update(request.getHeadline(), request.getBio(), 
                request.getGreetingEnabled() != null ? request.getGreetingEnabled() : true);
        Profile updatedProfile = profileRepository.save(profile);
        
        // TODO: 실제 관심사와 경력 데이터를 가져와서 전달해야 함
        return ProfileResponse.from(updatedProfile, List.of(), List.of());
    }

    public ProfileResponse getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "프로필을 찾을 수 없습니다."));

        // TODO: 실제 관심사와 경력 데이터를 가져와서 전달해야 함
        return ProfileResponse.from(profile, List.of(), List.of());
    }

    /**
     * 프로필 조회수 증가
     */
    @Transactional
    public void incrementViewCount(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "프로필을 찾을 수 없습니다."));
        
        profile.incrementViewCount();
        profileRepository.save(profile);
    }

    /**
     * 사용자 ID로 프로필 조회수 증가 (Repository 직접 호출)
     */
    @Transactional
    public void incrementViewCountByUserId(Long userId) {
        profileRepository.incrementViewCount(userId);
    }
}
