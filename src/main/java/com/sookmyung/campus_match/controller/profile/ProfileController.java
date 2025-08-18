package com.sookmyung.campus_match.controller.profile;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.post.PostSummaryResponse;
import com.sookmyung.campus_match.dto.profile.ProfileCreateRequest;
import com.sookmyung.campus_match.dto.profile.ProfileResponse;
import com.sookmyung.campus_match.dto.profile.ProfileUpdateRequest;
import com.sookmyung.campus_match.dto.user.InterestResponse;
import com.sookmyung.campus_match.service.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 프로필 관련 컨트롤러
 */
@Tag(name = "Profile", description = "프로필 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final ProfileService profileService;

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
            @ApiResponse(responseCode = "409", description = "이미 프로필이 존재함")
    })
    @PostMapping("/profiles")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> createProfile(
            @Valid @RequestBody ProfileCreateRequest request) {
        
        // TODO: 현재 사용자 정보를 가져와서 전달
        ProfileResponse profile = profileService.createProfile(request, "current-user");
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/profiles/" + profile.getUserId())
                .body(ApiEnvelope.created(profile));
    }

    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음")
    })
    @PutMapping("/profiles/{id}")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> updateProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest request) {
        
        // TODO: 현재 사용자 정보를 가져와서 전달
        ProfileResponse profile = profileService.updateProfile(id, request, "current-user");
        return ResponseEntity.ok(ApiEnvelope.success(profile));
    }

    @Operation(summary = "프로필 부분 수정", description = "프로필을 부분적으로 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음")
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

    @Operation(summary = "프로필 조회", description = "프로필을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음")
    })
    @GetMapping("/profiles/{id}")
    public ResponseEntity<ApiEnvelope<ProfileResponse>> getProfile(
            @Parameter(description = "프로필 ID", example = "1")
            @PathVariable Long id) {
        
        ProfileResponse profile = profileService.getProfile(id);
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
                            """)))
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
            @ApiResponse(responseCode = "401", description = "인증 실패")
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
