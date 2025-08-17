package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.common.enums.PostCategory;
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
    
    List<Post> findByCategory(PostCategory category);
    
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
    
    Page<Post> findByCategory(PostCategory category, Pageable pageable);
    
    @Query("""
      select p from Post p
      where lower(p.postTitle) like concat('%', :qLower, '%')
         or p.postContent like concat('%', :q, '%')
    """)
    Page<Post> search(@Param("qLower") String qLower, @Param("q") String q, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isClosed = false")
    Page<Post> findByClosedFalse(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isClosed = false")
    Page<Post> findByCategoryAndClosedFalse(@Param("category") PostCategory category, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.isClosed = false")
    Page<Post> findByAuthor_IdAndClosedFalse(@Param("authorId") Long authorId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    int incrementViews(@Param("postId") Long postId);
    
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    Page<Post> findAllActive(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.isClosed = false")
    Page<Post> findAllActiveAndOpen(Pageable pageable);

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

    // SearchService에서 호출하는 메서드들
    @Query("SELECT p FROM Post p WHERE " +
           "(:keyword IS NULL OR (p.postTitle LIKE %:keyword% OR p.postContent LIKE %:keyword% OR p.author.name LIKE %:keyword%)) AND " +
           "(:category IS NULL OR p.category = :category)")
    Page<Post> searchByKeywordAndCategory(@Param("keyword") String keyword, 
                                         @Param("category") PostCategory category, 
                                         Pageable pageable);
    

    
    @Query("SELECT p FROM Post p ORDER BY p.viewCount DESC, p.likeCount DESC LIMIT :limit")
    List<Post> findPopularPosts(@Param("limit") int limit);
    
    @Query("SELECT p FROM Post p WHERE p.category = :category ORDER BY p.createdAt DESC LIMIT :limit")
    List<Post> findLatestByCategory(@Param("category") PostCategory category, @Param("limit") int limit);
    
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC LIMIT :limit")
    List<Post> findLatestPosts(@Param("limit") int limit);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.isClosed = false")
    long countByIsClosedFalse();
}
