package com.sookmyung.campus_match.domain.recommendation;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 추천 매핑 엔티티
 * - 특정 사용자(user)에게 추천되는 다른 사용자(recommendedUser)를 저장
 * - AI 기반 추천 결과를 DB에 캐싱/저장하여 빠른 조회 가능
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_recommendations")
public class UserRecommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 추천을 받는 사용자

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recommended_user_id", nullable = false)
    private User recommendedUser; // 추천된 사용자

    @Column(nullable = false)
    private double similarityScore; // 추천 유사도 점수 (0~1)
}
