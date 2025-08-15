package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthor_Id(Long userId);
    
    List<Post> findByCategory_Id(Long categoryId);
    
    @Query("SELECT p FROM Post p WHERE " +
           "(:title IS NULL OR p.postTitle LIKE %:title%) AND " +
           "(:content IS NULL OR p.postContent LIKE %:content%)")
    Page<Post> searchByTitleOrContent(@Param("title") String title, 
                                      @Param("content") String content, 
                                      Pageable pageable);
    
    Page<Post> findByIsClosed(Boolean isClosed, Pageable pageable);
    
    List<Post> findByAuthor_IdOrderByCreatedAtDesc(Long userId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    Page<Post> findByAuthor_Id(Long userId, Pageable pageable);
    
    Page<Post> findByCategory_Id(Long categoryId, Pageable pageable);
    
    @Query("""
      select p from Post p
      where lower(p.postTitle) like concat('%', :qLower, '%')
         or p.postContent like concat('%', :q, '%')
    """)
    Page<Post> search(@Param("qLower") String qLower, @Param("q") String q, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isClosed = false")
    Page<Post> findByClosedFalse(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND p.isClosed = false")
    Page<Post> findByCategory_IdAndClosedFalse(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.isClosed = false")
    Page<Post> findByAuthor_IdAndClosedFalse(@Param("authorId") Long authorId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    int incrementViews(@Param("postId") Long postId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Query("SELECT p FROM Post p WHERE p.postTitle LIKE %:keyword%")
    Page<Post> findByTitleContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("""
      select p from Post p 
      where p.postTitle like concat('%', :keyword, '%') 
         or p.postContent like concat('%', :keyword, '%')
    """)
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            @Param("keyword") String keyword, 
            Pageable pageable);
}
