package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostLike;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 중첩 속성 표기를 사용한 메서드들
    List<PostLike> findByPost_Id(Long postId);
    
    List<PostLike> findByUser_Id(Long userId);
    
    Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);
    
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    
    long countByPost_Id(Long postId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    boolean existsByPostAndUser(Post post, User user);
    
    boolean existsByUserAndPost(User user, Post post);
    
    void deleteByUserAndPost(User user, Post post);
}
