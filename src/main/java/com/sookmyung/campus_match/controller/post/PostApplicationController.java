package com.sookmyung.campus_match.controller.post;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.post.PostApplicationRequest;
import com.sookmyung.campus_match.dto.application.PostApplicationResponse;
import com.sookmyung.campus_match.service.post.PostApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 게시글 지원 관련 컨트롤러
 */
@Tag(name = "Post Applications", description = "게시글 지원 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostApplicationController {

    private final PostApplicationService postApplicationService;

    @Operation(summary = "게시글 지원", description = "게시글에 지원합니다. 승인된 사용자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "지원 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "created",
                              "data": {
                                "id": 1,
                                "postId": 10,
                                "applicantId": 5,
                                "message": "프론트엔드 개발 경험이 있어서 도움이 될 것 같습니다.",
                                "status": "PENDING",
                                "createdAt": "2025-01-27T10:30:00"
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 지원한 게시글")
    })
    @PostMapping("/{id}/apply")
    public ResponseEntity<ApiEnvelope<PostApplicationResponse>> applyToPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long currentUserId,
            @Valid @RequestBody PostApplicationRequest request) {
        
        PostApplicationResponse application = postApplicationService.applyToPost(id, request, currentUserId);
        URI location = URI.create("/api/posts/" + id + "/applications/" + application.getId());
        return ResponseEntity.created(location).body(ApiEnvelope.created(application));
    }

    @Operation(summary = "모집 마감", description = "게시글 모집을 마감합니다. 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모집 마감 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 마감된 게시글")
    })
    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiEnvelope<Void>> closePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long currentUserId) {
        
        postApplicationService.closePost(id, currentUserId);
        return ResponseEntity.ok(ApiEnvelope.okMessage());
    }

    @Operation(summary = "지원 목록 조회", description = "게시글의 지원 목록을 페이징하여 조회합니다. 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 정렬 파라미터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/{id}/applications")
    public ResponseEntity<ApiEnvelope<PageResponse<PostApplicationResponse>>> getApplications(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long currentUserId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 (필드,방향)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        PageResponse<PostApplicationResponse> applications = postApplicationService.getApplications(id, currentUserId, page, size, sort);
        return ResponseEntity.ok(ApiEnvelope.success(applications));
    }

    @Operation(summary = "지원 수락", description = "지원을 수락합니다. 게시글 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 수락 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 지원을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 처리된 지원")
    })
    @PostMapping("/{id}/applications/{applicantId}/accept")
    public ResponseEntity<ApiEnvelope<Void>> acceptApplication(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "지원자 ID", example = "5")
            @PathVariable Long applicantId,
            @RequestHeader("X-USER-ID") Long currentUserId) {
        
        postApplicationService.acceptApplication(id, applicantId, currentUserId);
        return ResponseEntity.ok(ApiEnvelope.okMessage());
    }

    @Operation(summary = "지원 거절", description = "지원을 거절합니다. 게시글 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지원 거절 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 지원을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 처리된 지원")
    })
    @PostMapping("/{id}/applications/{applicantId}/reject")
    public ResponseEntity<ApiEnvelope<Void>> rejectApplication(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "지원자 ID", example = "5")
            @PathVariable Long applicantId,
            @RequestHeader("X-USER-ID") Long currentUserId) {
        
        postApplicationService.rejectApplication(id, applicantId, currentUserId);
        return ResponseEntity.ok(ApiEnvelope.okMessage());
    }
}
