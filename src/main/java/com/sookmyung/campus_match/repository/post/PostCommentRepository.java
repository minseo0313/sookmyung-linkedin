package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostComment;
import com.sookmyung.campus_match.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 특정 게시글의 모든 댓글 조회 (작성일 기준 정렬)
    List<PostComment> findByPostOrderByCreatedAtAsc(Post post);

    // 게시글 ID로 댓글 목록 조회
    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 작성자의 댓글 조회
    List<PostComment> findByUserId(Long userId);

    // 댓글 개수 조회
    long countByPostId(Long postId);
}
