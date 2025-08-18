package com.sookmyung.campus_match.controller.post;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.comment.PostCommentCreateRequest;
import com.sookmyung.campus_match.dto.comment.PostCommentResponse;
import com.sookmyung.campus_match.service.post.PostCommentService;
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
 * 게시글 댓글 관련 컨트롤러
 */
@Tag(name = "Comments", description = "게시글 댓글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostCommentController {

    private final PostCommentService postCommentService;

    @Operation(summary = "댓글 생성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "created",
                              "data": {
                                "id": 1,
                                "content": "관심있습니다! 연락드릴게요.",
                                "postId": 1,
                                "authorId": 2,
                                "authorName": "김철수",
                                "authorDepartment": "컴퓨터학부",
                                "createdAt": "2025-01-27T10:30:00",
                                "updatedAt": "2025-01-27T10:30:00"
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiEnvelope<PostCommentResponse>> createComment(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @RequestHeader("X-USER-ID") Long currentUserId,
            @Valid @RequestBody PostCommentCreateRequest request) {
        
        PostCommentResponse comment = postCommentService.createComment(request, postId, currentUserId.toString());
        URI location = URI.create("/api/posts/" + postId + "/comments/" + comment.getId());
        return ResponseEntity.created(location).body(ApiEnvelope.created(comment));
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 정렬 파라미터"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiEnvelope<PageResponse<PostCommentResponse>>> getComments(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 (필드,방향)", example = "createdAt,asc")
            @RequestParam(defaultValue = "createdAt,asc") String sort) {
        
        PageResponse<PostCommentResponse> comments = postCommentService.getComments(postId, page, size, sort);
        return ResponseEntity.ok(ApiEnvelope.success(comments));
    }
}
