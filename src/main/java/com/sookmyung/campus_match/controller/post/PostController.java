package com.sookmyung.campus_match.controller.post;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.dto.post.PostContentSuggestionRequest;
import com.sookmyung.campus_match.dto.post.PostContentSuggestionResponse;
import com.sookmyung.campus_match.dto.post.PostCreateRequest;
import com.sookmyung.campus_match.dto.post.PostDetailResponse;
import com.sookmyung.campus_match.dto.post.PostSummaryResponse;
import com.sookmyung.campus_match.dto.post.PostUpdateRequest;
import com.sookmyung.campus_match.dto.like.PostLikeCountResponse;
import com.sookmyung.campus_match.dto.application.PostApplicationRequest;
import com.sookmyung.campus_match.dto.application.PostApplicationResponse;
import com.sookmyung.campus_match.service.post.PostService;
import com.sookmyung.campus_match.util.security.RequiresApproval;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    @Operation(summary = "AI 게시글 작성 도움", description = "입력값을 기반으로 게시글 내용을 추천합니다.")
    @PostMapping("/suggest-content")
    @RequiresApproval(message = "승인된 사용자만 AI 글 작성 도움 기능을 사용할 수 있습니다.")
    public ResponseEntity<ApiResponse<PostContentSuggestionResponse>> suggestContent(
            @Valid @RequestBody PostContentSuggestionRequest request) {
        
        String suggestedContent = postService.generatePostContentSuggestion(
                request.getCategory(),
                request.getRequiredRoles(),
                request.getDuration(),
                request.getRecruitCount() != null ? request.getRecruitCount() : 1
        );
        
        PostContentSuggestionResponse response = PostContentSuggestionResponse.of(
                suggestedContent, 
                request.getCategory() != null ? request.getCategory() : "기본"
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @PostMapping
    @RequiresApproval(message = "승인된 사용자만 게시글을 작성할 수 있습니다.")
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // UserDetails에서 userId 추출 (학번으로 사용자 조회)
        User user = userRepository.findByStudentId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Post post = postService.createPost(request, user.getId());
        PostDetailResponse response = PostDetailResponse.from(post);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@PathVariable Long postId) {
        Post post = postService.getPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        // 조회수 증가
        postService.incrementViews(postId);
        
        PostDetailResponse response = PostDetailResponse.from(post);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/{postId}")
    @RequiresApproval(message = "승인된 사용자만 게시글을 수정할 수 있습니다.")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request) {
        
        Post post = postService.updatePost(postId, request);
        PostDetailResponse response = PostDetailResponse.from(post);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPosts(
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=createdAt,desc")
            Pageable pageable) {
        
        Page<Post> posts = postService.getPosts(pageable);
        Page<PostSummaryResponse> response = posts.map(PostSummaryResponse::from);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    @RequiresApproval(message = "승인된 사용자만 게시글을 삭제할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }

    @Operation(summary = "게시글 좋아요 추가", description = "게시글에 좋아요를 추가합니다.")
    @PostMapping("/{postId}/like")
    @RequiresApproval(message = "승인된 사용자만 좋아요를 할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        postService.likePost(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("좋아요가 추가되었습니다."));
    }

    @Operation(summary = "게시글 좋아요 수 조회", description = "게시글의 좋아요 개수를 조회합니다.")
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<ApiResponse<PostLikeCountResponse>> getLikeCount(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId) {
        PostLikeCountResponse likeCount = postService.getLikeCount(postId);
        return ResponseEntity.ok(ApiResponse.success(likeCount));
    }

    @Operation(summary = "게시글 지원", description = "게시글에 팀 지원 요청을 합니다.")
    @PostMapping("/{postId}/apply")
    @RequiresApproval(message = "승인된 사용자만 게시글에 지원할 수 있습니다.")
    public ResponseEntity<ApiResponse<PostApplicationResponse>> applyToPost(
            @PathVariable Long postId,
            @Valid @RequestBody PostApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        PostApplicationResponse application = postService.applyToPost(postId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    @Operation(summary = "게시글 지원자 목록", description = "게시글의 지원자 목록을 조회합니다.")
    @GetMapping("/{postId}/applications")
    @RequiresApproval(message = "승인된 사용자만 지원자 목록을 볼 수 있습니다.")
    public ResponseEntity<ApiResponse<List<PostApplicationResponse>>> getApplications(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<PostApplicationResponse> applications = postService.getApplications(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @Operation(summary = "지원 수락", description = "지원자의 신청을 수락합니다.")
    @PostMapping("/{postId}/applications/{applicantId}/accept")
    @RequiresApproval(message = "승인된 사용자만 지원을 수락할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> acceptApplication(
            @PathVariable Long postId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        postService.acceptApplication(postId, applicantId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("지원이 수락되었습니다."));
    }

    @Operation(summary = "지원 거절", description = "지원자의 신청을 거절합니다.")
    @PostMapping("/{postId}/applications/{applicantId}/reject")
    @RequiresApproval(message = "승인된 사용자만 지원을 거절할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> rejectApplication(
            @PathVariable Long postId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        postService.rejectApplication(postId, applicantId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("지원이 거절되었습니다."));
    }

    @Operation(summary = "모집 마감", description = "게시글의 모집을 마감합니다.")
    @PatchMapping("/{postId}/close")
    @RequiresApproval(message = "승인된 사용자만 모집을 마감할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> closePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        postService.closePost(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("모집이 마감되었습니다."));
    }
}
