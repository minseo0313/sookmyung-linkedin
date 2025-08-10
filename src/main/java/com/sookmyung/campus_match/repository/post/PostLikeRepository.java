package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostLike;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 게시글별 좋아요 목록 조회
    List<PostLike> findByPost(Post post);
    List<PostLike> findByPostId(Long postId);

    // 특정 유저가 누른 좋아요 조회
    List<PostLike> findByUser(User user);
    List<PostLike> findByUserId(Long userId);

    // 유저가 특정 게시글에 좋아요 눌렀는지 여부
    boolean existsByUserAndPost(User user, Post post);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // Post + User로 조회 (서비스에서 사용)
    boolean existsByPostAndUser(Post post, User user);

    // 좋아요 단건 삭제 (유저가 누른 좋아요 취소)
    void deleteByUserAndPost(User user, Post post);

    // 좋아요 개수
    long countByPostId(Long postId);
}
