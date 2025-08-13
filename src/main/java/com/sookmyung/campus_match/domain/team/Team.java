package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 팀 엔티티
 * - 게시글(Post) 매칭 확정 시 자동 생성
 * - 운영진(팀장/부팀장 등)과 팀원 정보는 TeamMember에서 관리
 * - 팀 비서(스케줄, 업무 할당) 기능과 연계
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "teams",
        indexes = {
                @Index(name = "idx_teams_name", columnList = "name"),
                @Index(name = "idx_teams_created", columnList = "created_at")
        }
)
public class Team extends BaseEntity {

    /** 팀 이름 (기본: 매칭된 게시글 제목) */
    @Column(nullable = false, length = 150)
    private String name;

    /** 팀 설명(선택) */
    @Column(length = 500)
    private String description;

    /** 팀 생성자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false,
            foreignKey = @ForeignKey(name = "fk_teams_creator"))
    private User createdBy;

    /** 연락처 공개 여부 (매칭 성사 시 운영진·팀원간 공유) */
    @Column(nullable = false)
    @Builder.Default
    private boolean contactShared = true;

    /** 매칭된 게시글 ID (nullable) */
    @Column
    private Long matchedPostId;

    /** (옵션) 매칭된 게시글 연관. Post → Team 단방향이면 FK만 보관 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_teams_post"))
    private Post post;

    /** 팀 멤버 목록 */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamMember> members = new ArrayList<>();

    /** 팀 스케줄 목록 */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamSchedule> schedules = new ArrayList<>();

    /* ==========================
       연관관계 편의 메서드
       ========================== */

    public void addMember(TeamMember member) {
        if (member != null) {
            members.add(member);
            member.setTeam(this);
        }
    }

    public void removeMember(TeamMember member) {
        if (member != null) {
            members.remove(member);
            member.setTeam(null);
        }
    }

    public void addSchedule(TeamSchedule schedule) {
        if (schedule != null) {
            schedules.add(schedule);
            schedule.setTeam(this);
        }
    }

    public void removeSchedule(TeamSchedule schedule) {
        if (schedule != null) {
            schedules.remove(schedule);
            schedule.setTeam(null);
        }
    }

    /**
     * 팀 생성자 설정
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 팀 생성자 ID 조회
     */
    public Long getCreatedById() {
        return createdBy != null ? createdBy.getId() : null;
    }
}
