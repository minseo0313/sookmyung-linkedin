package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 중첩 속성 표기를 사용한 메서드들
    List<PostComment> findByPost_Id(Long postId);
    
    List<PostComment> findByUser_Id(Long userId);
    
    List<PostComment> findByPost_IdOrderByCreatedAtDesc(Long postId);
    
    Page<PostComment> findByPost_IdOrderByCreatedAtAsc(Long postId, Pageable pageable);
    
    long countByPost_Id(Long postId);
    
    long countByUser_Id(Long userId);
    
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    
    void deleteByPost_Id(Long postId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.createdAt ASC")
    List<PostComment> findByPost_IdOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.user.id = :userId ORDER BY pc.createdAt DESC")
    Page<PostComment> findByUser_IdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // 안전한 대안 메서드들 (@Query 사용)
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId")
    List<PostComment> findByPostIdSafe(@Param("postId") Long postId);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.user.id = :userId")
    List<PostComment> findByUserIdSafe(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.id = :postId")
    long countByPostIdSafe(@Param("postId") Long postId);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.createdAt DESC")
    List<PostComment> findByPostIdOrderByCreatedAtDescSafe(@Param("postId") Long postId);
}
