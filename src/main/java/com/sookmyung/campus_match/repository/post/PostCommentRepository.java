package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 게시글별 댓글 목록 (시간순)
    List<PostComment> findByPost_IdOrderByCreatedAtAsc(Long postId);
    
    // 게시글별 댓글 목록 (페이징)
    Page<PostComment> findByPost_IdOrderByCreatedAtAsc(Long postId, Pageable pageable);
    
    // 작성자별 댓글 목록 (최신순)
    Page<PostComment> findByAuthor_IdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // 게시글별 댓글 수
    long countByPost_Id(Long postId);
    
    // 작성자별 댓글 수
    long countByAuthor_Id(Long authorId);
}
