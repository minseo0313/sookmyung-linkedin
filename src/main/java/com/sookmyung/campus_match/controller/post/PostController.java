package com.sookmyung.campus_match.controller.post;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.post.*;
import com.sookmyung.campus_match.service.post.PostService;
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
 * 게시글 관련 컨트롤러
 */
@Tag(name = "Posts", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다. 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "created",
                              "data": {
                                "id": 1,
                                "category": "PROJECT",
                                "title": "프로젝트 팀원 모집",
                                "content": "웹 개발 프로젝트 팀원을 모집합니다...",
                                "requiredRoles": "프론트엔드 2명, 백엔드 1명",
                                "recruitmentCount": 3,
                                "duration": "3개월",
                                "linkUrl": "https://github.com/project",
                                "imageUrl": "https://example.com/image.jpg",
                                "isClosed": false,
                                "viewCount": 0,
                                "likeCount": 0,
                                "commentCount": 0,
                                "authorId": 1,
                                "authorName": "홍길동",
                                "authorDepartment": "컴퓨터학부",
                                "authorStudentId": "20240001",
                                "createdAt": "2025-01-27T10:30:00",
                                "updatedAt": "2025-01-27T10:30:00"
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<ApiEnvelope<PostDetailResponse>> createPost(
            @RequestHeader("X-USER-ID") Long currentUserId,
            @Valid @RequestBody PostCreateRequest request) {
        
        PostDetailResponse post = postService.createPost(request, currentUserId);
        URI location = URI.create("/api/posts/" + post.getId());
        return ResponseEntity.created(location).body(ApiEnvelope.created(post));
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다. 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiEnvelope<PostDetailResponse>> updatePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long currentUserId,
            @Valid @RequestBody PostUpdateRequest request) {
        
        PostDetailResponse post = postService.updatePost(id, request, currentUserId);
        return ResponseEntity.ok(ApiEnvelope.success(post));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. 작성자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long currentUserId) {
        
        postService.deletePost(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 정렬 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<ApiEnvelope<PageResponse<PostSummaryResponse>>> getPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 (필드,방향)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        PageResponse<PostSummaryResponse> posts = postService.getPosts(page, size, sort);
        return ResponseEntity.ok(ApiEnvelope.success(posts));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 정보를 조회합니다. 조회수 증가.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<PostDetailResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id) {
        
        PostDetailResponse post = postService.getPost(id);
        return ResponseEntity.ok(ApiEnvelope.success(post));
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가합니다. 중복 호출 시 무시(멱등성 보장).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiEnvelope<Void>> likePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long currentUserId) {
        
        postService.likePost(id, currentUserId);
        return ResponseEntity.ok(ApiEnvelope.okMessage());
    }

    @Operation(summary = "게시글 좋아요 수 조회", description = "게시글의 좋아요 수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 수 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "success",
                              "data": {
                                "postId": 1,
                                "likeCount": 25
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}/likes/count")
    public ResponseEntity<ApiEnvelope<PostLikeCountResponse>> getPostLikeCount(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id) {
        
        PostLikeCountResponse likeCount = postService.getPostLikeCount(id);
        return ResponseEntity.ok(ApiEnvelope.success(likeCount));
    }
}
