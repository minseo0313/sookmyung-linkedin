package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 팀 비서(Secretary) 엔티티
 * - 팀 매칭 확정 시 자동 생성되어 일정·업무 관리 기능을 보조
 * - v1에서는 단순히 팀별 1개 레코드로 존재, 실제 스케줄/업무는 TeamSchedule, ScheduleAssignment에서 관리
 * - 추후 AI 스케줄 추천, 자동 할당 등 확장 가능
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "team_secretaries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_team_secretaries_team", columnNames = {"team_id"})
        },
        indexes = {
                @Index(name = "idx_team_secretaries_team_id", columnList = "team_id")
        }
)
public class TeamSecretary extends BaseEntity {

    /** 소속 팀 (1:1) */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_team_secretaries_team"))
    private Team team;

    /** 비서 이름 또는 닉네임 (선택) */
    @Column(length = 100)
    private String name;

    /** 비서 소개/설명 (선택) */
    @Column(length = 500)
    private String description;

    /** 활성 여부 */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void setTeam(Team team) {
        this.team = team;
    }

    /* ==========================
       상태 변경 메서드
       ========================== */

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void rename(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void changeDescription(String description) {
        this.description = description;
    }
}
