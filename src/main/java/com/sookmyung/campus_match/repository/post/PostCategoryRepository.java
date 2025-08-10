package com.sookmyung.campus_match.repository.post;

import com.sookmyung.campus_match.domain.post.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    // 이름으로 단건 조회
    Optional<PostCategory> findByName(String name);
    Optional<PostCategory> findByNameIgnoreCase(String name);

    // 중복 체크
    boolean existsByNameIgnoreCase(String name);

    // 이름 기준 정렬
    List<PostCategory> findAllByOrderByNameAsc();
}
