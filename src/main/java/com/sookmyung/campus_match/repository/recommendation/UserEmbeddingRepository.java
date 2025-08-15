package com.sookmyung.campus_match.repository.recommendation;

import com.sookmyung.campus_match.domain.recommendation.UserEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEmbeddingRepository extends JpaRepository<UserEmbedding, Long> {

    Optional<UserEmbedding> findByUser_Id(Long userId);
    
    boolean existsByUserId(Long userId);
}
