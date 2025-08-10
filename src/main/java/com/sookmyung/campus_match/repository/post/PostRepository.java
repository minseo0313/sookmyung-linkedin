package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 작성자 기준(내 글 모아보기)
    Page<Post> findByAuthor_Id(Long authorId, Pageable pageable);
    Page<Post> findByAuthor_IdAndClosedFalse(Long authorId, Pageable pageable);

    // 전체/카테고리별 목록 (모집 중만)
    Page<Post> findByClosedFalse(Pageable pageable);
    Page<Post> findByCategory_IdAndClosedFalse(Long categoryId, Pageable pageable);

    // 검색(제목/본문 부분 일치 + 페이징)
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable
    );

    // 제목 검색
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 최신 상위 N (메인 노출용)
    List<Post> findTop10ByClosedFalseOrderByCreatedAtDesc();
    List<Post> findTop10ByCategory_IdAndClosedFalseOrderByCreatedAtDesc(Long categoryId);

    // 조회수 +1 (상세 진입 시 사용)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.views = p.views + 1 where p.id = :postId")
    int incrementViews(Long postId);
}
