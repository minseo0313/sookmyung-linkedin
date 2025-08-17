package com.sookmyung.campus_match.controller.post;

import com.sookmyung.campus_match.dto.comment.PostCommentCreateRequest;
import com.sookmyung.campus_match.dto.comment.PostCommentResponse;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.service.post.PostCommentService;
import com.sookmyung.campus_match.util.security.RequiresApproval;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글", description = "게시글 댓글 관련 API")
@RestController
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다. (학생 A는 최대 2개까지만)")
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<PostCommentResponse>>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        List<PostCommentResponse> comments = postCommentService.getCommentsForPost(postId, username);
        
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    // Deprecated: 기존 오타 경로 지원 (302 리다이렉트)
    @GetMapping("/api/posts/{postId}/coments")
    public ResponseEntity<Void> getCommentsRedirect(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", "/api/posts/" + postId + "/comments")
                .build();
    }

    @Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다. (승인된 사용자만)")
    @PostMapping("/api/posts/{postId}/comments")
    @RequiresApproval(message = "승인된 사용자만 댓글을 작성할 수 있습니다.")
    public ResponseEntity<ApiResponse<PostCommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody PostCommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        PostCommentResponse comment = postCommentService.createComment(request, postId, username);
        
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다. (작성자만)")
    @PutMapping("/api/posts/{postId}/comments/{commentId}")
    @RequiresApproval(message = "승인된 사용자만 댓글을 수정할 수 있습니다.")
    public ResponseEntity<ApiResponse<PostCommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody PostCommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        PostCommentResponse comment = postCommentService.updateComment(commentId, request.getContent(), username);
        
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. (작성자만)")
    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    @RequiresApproval(message = "승인된 사용자만 댓글을 삭제할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        postCommentService.deleteComment(commentId, username);
        
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다."));
    }

    @Operation(summary = "내 댓글 목록", description = "내가 작성한 댓글 목록을 조회합니다.")
    @GetMapping("/api/posts/{postId}/comments/my")
    public ResponseEntity<ApiResponse<Page<PostCommentResponse>>> getMyComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        
        String username = userDetails.getUsername();
        Page<PostCommentResponse> comments = postCommentService.getUserComments(username, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
}
