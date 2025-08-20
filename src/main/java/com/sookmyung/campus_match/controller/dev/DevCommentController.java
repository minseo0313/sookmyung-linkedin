package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dev 환경용 댓글 스텁 컨트롤러
 * 실제 댓글 수정/삭제 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Profile("dev")
public class DevCommentController {

    private final CurrentUserResolver currentUserResolver;
    
    // In-memory 캐시
    private final ConcurrentHashMap<Long, CommentResponse> comments = new ConcurrentHashMap<>();
    private final AtomicLong commentIdCounter = new AtomicLong(1);

    /**
     * Dev 환경용 댓글 수정 스텁
     */
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiEnvelope<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request) {
        log.info("Dev 환경 - 댓글 수정 스텁 호출: postId={}, commentId={}", postId, commentId);
        
        Long userId = currentUserResolver.currentUserId();
        
        CommentResponse response = CommentResponse.builder()
                .commentId(commentId)
                .postId(postId)
                .userId(userId)
                .content(request.content)
                .updatedAt(Instant.now().toString())
                .build();
        
        comments.put(commentId, response);
        
        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    /**
     * Dev 환경용 댓글 삭제 스텁
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        log.info("Dev 환경 - 댓글 삭제 스텁 호출: postId={}, commentId={}", postId, commentId);
        
        comments.remove(commentId);
        
        return ResponseEntity.noContent().build();
    }

    // 내부 DTO 클래스들
    public static class CommentUpdateRequest {
        public String content;
    }

    public static class CommentResponse {
        public Long commentId;
        public Long postId;
        public Long userId;
        public String content;
        public String updatedAt;

        public static CommentResponseBuilder builder() {
            return new CommentResponseBuilder();
        }

        public static class CommentResponseBuilder {
            private CommentResponse response = new CommentResponse();

            public CommentResponseBuilder commentId(Long commentId) {
                response.commentId = commentId;
                return this;
            }

            public CommentResponseBuilder postId(Long postId) {
                response.postId = postId;
                return this;
            }

            public CommentResponseBuilder userId(Long userId) {
                response.userId = userId;
                return this;
            }

            public CommentResponseBuilder content(String content) {
                response.content = content;
                return this;
            }

            public CommentResponseBuilder updatedAt(String updatedAt) {
                response.updatedAt = updatedAt;
                return this;
            }

            public CommentResponse build() {
                return response;
            }
        }
    }
}

