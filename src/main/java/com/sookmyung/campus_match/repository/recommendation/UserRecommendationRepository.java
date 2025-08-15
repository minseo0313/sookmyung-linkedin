package com.sookmyung.campus_match.repository.recommendation;

import com.sookmyung.campus_match.domain.recommendation.UserRecommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {

    List<UserRecommendation> findByUser_Id(Long userId);
    
    List<UserRecommendation> findByRecommendedUserId(Long recommendedUserId);
    
    @Query("SELECT ur FROM UserRecommendation ur WHERE " +
           "ur.user.id = :userId ORDER BY ur.similarityScore DESC")
    Page<UserRecommendation> findByUserIdOrderBySimilarityScore(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT ur FROM UserRecommendation ur WHERE " +
           "ur.user.id = :userId AND ur.similarityScore >= :minScore")
    List<UserRecommendation> findByUserIdAndMinSimilarityScore(@Param("userId") Long userId, @Param("minScore") Double minScore);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Modifying
    void deleteByUserId(Long userId);
}
