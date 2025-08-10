package com.sookmyung.campus_match.repository.recommendation;

import com.sookmyung.campus_match.domain.recommendation.UserRecommendation;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {

    // 특정 유저의 추천 목록 조회
    List<UserRecommendation> findByUser(User user);
    List<UserRecommendation> findByUserId(Long userId);

    // 추천 대상 유저로 검색
    List<UserRecommendation> findByRecommendedUserId(Long recommendedUserId);

    // 유저 + 추천 대상 유저 단건 조회
    Optional<UserRecommendation> findByUserIdAndRecommendedUserId(Long userId, Long recommendedUserId);

    // 중복 체크
    boolean existsByUserIdAndRecommendedUserId(Long userId, Long recommendedUserId);

    // 특정 유저의 모든 추천 기록 삭제
    void deleteByUserId(Long userId);
}
