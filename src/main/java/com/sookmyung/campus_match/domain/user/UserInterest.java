package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자-관심사 연결 엔티티
 * - 한 사용자는 여러 관심사를 가질 수 있음
 * - 하나의 관심사는 여러 사용자에게 연결될 수 있음
 * - User ↔ Interest = N:M 관계를 풀어주는 조인 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "user_interests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_interest", columnNames = {"user_id", "interest_id"})
        },
        indexes = {
                @Index(name = "idx_user_interest_user_id", columnList = "user_id"),
                @Index(name = "idx_user_interest_interest_id", columnList = "interest_id")
        }
)
public class UserInterest extends BaseEntity {

    /** 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_interest_user"))
    private User user;

    /** 관심사 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interest_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_interest_interest"))
    private Interest interest;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setUser(User user) {
        this.user = user;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }
}
