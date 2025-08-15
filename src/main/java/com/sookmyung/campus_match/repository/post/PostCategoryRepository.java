package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    Optional<PostCategory> findByCategoryName(String categoryName);
    
    boolean existsByCategoryName(String categoryName);
}
