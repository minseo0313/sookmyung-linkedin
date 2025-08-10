package com.sookmyung.campus_match.service.post;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.post.PostComment;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.user.enum_.ApprovalStatus;
import com.sookmyung.campus_match.dto.comment.PostCommentCreateRequest;
import com.sookmyung.campus_match.dto.comment.PostCommentResponse;
import com.sookmyung.campus_match.repository.post.PostCommentRepository;
import com.sookmyung.campus_match.repository.post.PostRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글의 댓글 목록 조회 (학생 A 권한 제한 적용)
     * - 학생 A: 최대 2개까지만 읽기 가능
     * - 학생 B: 모든 댓글 읽기 가능
     */
    public List<PostCommentResponse> getCommentsForPost(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        List<PostComment> comments = postCommentRepository.findByPost_IdOrderByCreatedAtAsc(postId);
        
        // 학생 A(PENDING)인 경우 댓글을 최대 2개까지만 반환
        if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
            log.info("학생 A {}가 게시글 {}의 댓글을 조회 (제한: 최대 2개)", username, postId);
            comments = comments.stream()
                    .limit(2)
                    .collect(Collectors.toList());
        }
        
        return comments.stream()
                .map(PostCommentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 작성 (승인된 사용자만 가능)
     */
    @Transactional
    public PostCommentResponse createComment(PostCommentCreateRequest request, Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // 승인되지 않은 사용자는 댓글 작성 불가
        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("승인된 사용자만 댓글을 작성할 수 있습니다.");
        }
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        PostComment comment = PostComment.builder()
                .post(post)
                .author(user)
                .content(request.getContent())
                .build();
        
        PostComment savedComment = postCommentRepository.save(comment);
        
        // 게시글의 댓글 수 증가
        post.increaseCommentCount(1);
        postRepository.save(post);
        
        return PostCommentResponse.from(savedComment);
    }

    /**
     * 댓글 수정 (작성자만 가능)
     */
    @Transactional
    public PostCommentResponse updateComment(Long commentId, String newContent, String username) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        
        // 작성자 확인
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }
        
        comment.edit(newContent);
        PostComment updatedComment = postCommentRepository.save(comment);
        
        return PostCommentResponse.from(updatedComment);
    }

    /**
     * 댓글 삭제 (작성자만 가능)
     */
    @Transactional
    public void deleteComment(Long commentId, String username) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        
        // 작성자 확인
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }
        
        // 소프트 삭제
        comment.softDelete();
        postCommentRepository.save(comment);
        
        // 게시글의 댓글 수 감소
        Post post = comment.getPost();
        post.increaseCommentCount(-1);
        postRepository.save(post);
    }

    /**
     * 사용자의 댓글 목록 조회
     */
    public Page<PostCommentResponse> getUserComments(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Page<PostComment> comments = postCommentRepository.findByAuthor_IdOrderByCreatedAtDesc(user.getId(), pageable);
        
        return comments.map(PostCommentResponse::from);
    }
}
