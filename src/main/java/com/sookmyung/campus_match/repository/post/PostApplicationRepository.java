package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostApplication;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.post.enum_.ApplicationStatus;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostApplicationRepository extends JpaRepository<PostApplication, Long> {

    // 특정 게시글의 지원자 목록
    List<PostApplication> findByPost(Post post);
    List<PostApplication> findByPostId(Long postId);

    // 특정 사용자가 지원한 글 목록
    List<PostApplication> findByUser(User user);
    List<PostApplication> findByUserId(Long userId);

    // 유저 + 게시글로 단건 조회 (중복 지원 방지)
    Optional<PostApplication> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);

    // 상태별 조회
    List<PostApplication> findByPostIdAndStatus(Long postId, ApplicationStatus status);

    // 지원자 수
    long countByPostId(Long postId);
}
