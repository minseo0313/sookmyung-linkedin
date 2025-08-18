package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostApplication;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostApplicationRepository extends JpaRepository<PostApplication, Long> {

    // 중첩 속성 표기를 사용한 메서드들
    List<PostApplication> findByPost_Id(Long postId);
    
    // 페이징을 위한 메서드
    Page<PostApplication> findByPost_Id(Long postId, Pageable pageable);
    
    List<PostApplication> findByApplicant_Id(Long userId);
    
    List<PostApplication> findByPost_IdAndStatus(Long postId, ApplicationStatus status);
    
    @Query("SELECT pa FROM PostApplication pa WHERE " +
           "pa.post.id = :postId AND " +
           "(:status IS NULL OR pa.status = :status)")
    Page<PostApplication> findByPost_IdAndStatus(@Param("postId") Long postId,
                                               @Param("status") ApplicationStatus status,
                                               Pageable pageable);
    
    boolean existsByPost_IdAndApplicant_Id(Long postId, Long userId);
    
    // 지원자 ID로 조회하는 메서드 (Optional 반환)
    Optional<PostApplication> findByPost_IdAndApplicant_Id(Long postId, Long applicantId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    List<PostApplication> findByPost(Post post);
    
    Optional<PostApplication> findByPostAndApplicant_Id(Post post, Long applicantId);
    
    boolean existsByPostAndApplicant(Post post, User applicant);
    
    boolean existsByPostAndApplicant_Id(Post post, Long applicantId);
    
    long countByPost_Id(Long postId);
}
