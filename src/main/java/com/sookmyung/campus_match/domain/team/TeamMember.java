package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.team.enum_.MemberRole;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 팀 구성원 엔티티
 * - 한 팀(Team)에 속한 사용자(User)와 그 역할(MemberRole)을 표현
 * - (team_id, user_id) 유니크: 동일 사용자의 중복 가입 방지
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "team_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_team_members_team_user", columnNames = {"team_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_team_members_team_id", columnList = "team_id"),
                @Index(name = "idx_team_members_user_id", columnList = "user_id"),
                @Index(name = "idx_team_members_role", columnList = "role"),
                @Index(name = "idx_team_members_created", columnList = "created_at")
        }
)
public class TeamMember extends BaseEntity {

    /** 소속 팀 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_team_members_team"))
    private Team team;

    /** 구성원 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_team_members_user"))
    private User user;

    /** 팀 내 역할 (OWNER/OPERATOR/MEMBER 등) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /* ==========================
       도메인 메서드
       ========================== */

    public void changeRole(MemberRole role) {
        if (role != null) {
            this.role = role;
        }
    }

    public boolean isOwner() {
        return this.role == MemberRole.OWNER;
    }

    public boolean isOperator() {
        return this.role == MemberRole.OPERATOR || this.role == MemberRole.OWNER;
    }
}
