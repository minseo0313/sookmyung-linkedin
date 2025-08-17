package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.MemberRole;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // TeamService에서 호출하는 필드들
    @Column(name = "role")
    private String role;

    // 호환성 메서드들
    public MemberRole getRole() {
        return this.memberRole;
    }

    public String getRoleString() {
        return this.role != null ? this.role : (this.memberRole != null ? this.memberRole.name() : null);
    }

    // 빌더 메서드 추가
    public static class TeamMemberBuilder {
        public TeamMemberBuilder role(String role) {
            this.role = role;
            this.memberRole = MemberRole.valueOf(role);
            return this;
        }

        public TeamMemberBuilder joinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
            return this;
        }
    }
}
