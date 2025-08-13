package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostApplication;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.post.enum_.ApplicationStatus;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostApplicationRepository extends JpaRepository<PostApplication, Long> {

    // 특정 게시글의 지원자 목록
    List<PostApplication> findByPost(Post post);
    
    // 특정 게시글 ID로 지원자 목록 조회
    @Query("select pa from PostApplication pa where pa.post.id = :postId")
    List<PostApplication> findByPostId(@Param("postId") Long postId);

    // 특정 사용자가 지원한 글 목록
    List<PostApplication> findByApplicant(User applicant);
    
    // 특정 사용자 ID로 지원한 글 목록 조회
    @Query("select pa from PostApplication pa where pa.applicant.id = :applicantId")
    List<PostApplication> findByApplicantId(@Param("applicantId") Long applicantId);

    // 유저 + 게시글로 단건 조회 (중복 지원 방지)
    Optional<PostApplication> findByApplicantAndPost(User applicant, Post post);
    boolean existsByApplicantAndPost(User applicant, Post post);
    
    // Post + Applicant ID로 조회
    @Query("""
        select pa from PostApplication pa
        where pa.post = :post
          and pa.applicant.id = :applicantId
        """)
    Optional<PostApplication> findByPostAndApplicantId(@Param("post") Post post,
                                                       @Param("applicantId") Long applicantId);
    
    // Post + Applicant로 조회 (서비스에서 사용)
    boolean existsByPostAndApplicant(Post post, User applicant);

    // 상태별 조회
    @Query("select pa from PostApplication pa where pa.post.id = :postId and pa.status = :status")
    List<PostApplication> findByPostIdAndStatus(@Param("postId") Long postId, @Param("status") ApplicationStatus status);

    // 지원자 수
    @Query("select count(pa) from PostApplication pa where pa.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);
}
