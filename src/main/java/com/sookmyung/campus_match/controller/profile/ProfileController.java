package com.sookmyung.campus_match.controller.profile;

import com.sookmyung.campus_match.domain.common.enums.InterestType;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.profile.ProfileCreateRequest;
import com.sookmyung.campus_match.dto.profile.ProfileResponse;
import com.sookmyung.campus_match.dto.profile.ProfileUpdateRequest;
import com.sookmyung.campus_match.service.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "프로필", description = "사용자 프로필 관련 API")
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 등록", description = "새로운 프로필을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @Valid @RequestBody ProfileCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ProfileResponse profile = profileService.createProfile(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "프로필 수정", description = "기존 프로필을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ProfileResponse profile = profileService.updateProfile(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id) {
        
        ProfileResponse profile = profileService.getProfile(id);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "관심사 목록", description = "사용 가능한 관심사 목록을 조회합니다.")
    @GetMapping("/interests")
    public ResponseEntity<ApiResponse<List<InterestType>>> getInterests() {
        List<InterestType> interests = Arrays.asList(InterestType.values());
        return ResponseEntity.ok(ApiResponse.success(interests));
    }

    @Operation(summary = "내 게시글 목록", description = "현재 사용자가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/{id}/posts")
    public ResponseEntity<ApiResponse<List<Object>>> getMyPosts(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // TODO: 실제 게시글 목록 조회 로직 구현 필요
        List<Object> posts = List.of(); // 임시 빈 리스트
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
}
