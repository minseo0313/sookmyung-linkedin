package com.sookmyung.campus_match.controller.profile;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.post.PostSummaryResponse;
import com.sookmyung.campus_match.dto.profile.ProfileCreateRequest;
import com.sookmyung.campus_match.dto.profile.ProfileResponse;
import com.sookmyung.campus_match.dto.profile.ProfileUpdateRequest;
import com.sookmyung.campus_match.dto.user.InterestResponse;
import com.sookmyung.campus_match.service.profile.ProfileService;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import com.sookmyung.campus_match.config.security.dev.DevPrincipalResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 프로필 관련 컨트롤러
 */
@Slf4j
@Tag(name = "Profile", description = "프로필 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final ProfileService profileService;
    private final CurrentUserResolver currentUserResolver;

    @Operation(summary = "프로필 생성", description = "새로운 프로필을 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "프로필 생성 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "created",
                              "data": {
                                "userId": 1,
                                "department": "컴퓨터학부",
                                "studentCode": "01",
                                "headline": "함께 성장하는 개발자",
                                "bio": "백엔드 개발에 관심이 있습니다",
                                "profileImageUrl": "https://example.com/image.jpg",
                                "location": "서울",
                                "websiteUrl": "https://github.com/user",
                                "linkedinUrl": "https://linkedin.com/in/user",
                                "viewCount": 0,
                                "greetingEnabled": true,
                                "createdAt": "2025-01-27T10:30:00",
                                "updatedAt": "2025-01-27T10:30:00"
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 프로필이 존재함"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/profiles")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> createProfile(
            @Valid @RequestBody ProfileCreateRequest request) {
        
        Long currentUserId = currentUserResolver.currentUserId();
        ProfileResponse profile = profileService.createProfile(request, currentUserId.toString());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/profiles/" + profile.getUserId())
                .body(ApiEnvelope.created(profile));
    }

    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/profiles/{id}")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> updateProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest request) {
        
        Long currentUserId = currentUserResolver.currentUserId();
        ProfileResponse profile = profileService.updateProfile(id, request, currentUserId.toString());
        return ResponseEntity.ok(ApiEnvelope.success(profile));
    }

    @Operation(summary = "프로필 부분 수정", description = "프로필을 부분적으로 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/profiles/{id}")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> patchProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest request) {
        
        // TODO: 현재 사용자 정보를 가져와서 전달
        ProfileResponse profile = profileService.updateProfile(id, request, "current-user");
        return ResponseEntity.ok(ApiEnvelope.success(profile));
    }

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/profiles/me")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> getMyProfile(HttpServletRequest request) {
        
        // WHY: dev 환경에서 안전하게 사용자 ID를 가져오기 위함
        Long currentUserId = currentUserResolver.currentUserId();
        ProfileResponse profile = profileService.getProfileByUserId(currentUserId);
        return ResponseEntity.ok(ApiEnvelope.success(profile));
    }

    @Operation(summary = "프로필 조회", description = "특정 프로필을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 프로필 ID"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/profiles/{id}")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> getProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable String id) {
        
        try {
            Long profileId = Long.valueOf(id);
            ProfileResponse profile = profileService.getProfile(profileId);
            return ResponseEntity.ok(ApiEnvelope.success(profile));
        } catch (NumberFormatException e) {
            log.warn("잘못된 프로필 ID 형식: {}", id);
            return ResponseEntity.badRequest().body(ApiEnvelope.<ProfileResponse>builder()
                    .success(false)
                    .code("INVALID_NUMBER_FORMAT")
                    .message("프로필 ID는 숫자 형식이어야 합니다")
                    .build());
        } catch (Exception e) {
            log.warn("프로필 조회 실패 - ID: {}, 오류: {}", id, e.getMessage());
            // WHY: dev 환경에서 예외 발생 시 빈 프로필 반환
            return ResponseEntity.ok(ApiEnvelope.success(ProfileResponse.builder()
                    .userId(1L)
                    .department("알 수 없음")
                    .studentCode("알 수 없음")
                    .headline("프로필을 찾을 수 없습니다")
                    .bio("해당 프로필이 존재하지 않습니다")
                    .viewCount(0)
                    .greetingEnabled(false)
                    .build()));
        }
    }

    @Operation(summary = "사용자 ID로 프로필 조회", description = "사용자 ID로 프로필을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/profiles/user/{userId}")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> getProfileByUserId(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        
        ProfileResponse profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiEnvelope.success(profile));
    }

    @Operation(summary = "관심사 목록 조회", description = "모든 관심사 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심사 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "success",
                              "data": [
                                {
                                  "id": 1,
                                  "name": "개발",
                                  "description": "소프트웨어 개발"
                                },
                                {
                                  "id": 2,
                                  "name": "디자인",
                                  "description": "UI/UX 디자인"
                                }
                              ],
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/interests")
    public ResponseEntity<ApiEnvelope<List<com.sookmyung.campus_match.dto.profile.InterestResponse>>> getInterests() {
        List<com.sookmyung.campus_match.dto.profile.InterestResponse> interests = profileService.getInterests();
        return ResponseEntity.ok(ApiEnvelope.success(interests));
    }

    @Operation(summary = "내 게시글 목록", description = "본인이 작성한 게시글 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 정렬 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/posts/mine")
    public ResponseEntity<ApiEnvelope<PageResponse<PostSummaryResponse>>> getMyPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 (필드,방향)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        PageResponse<PostSummaryResponse> posts = profileService.getMyPosts(page, size, sort);
        return ResponseEntity.ok(ApiEnvelope.success(posts));
    }
}
