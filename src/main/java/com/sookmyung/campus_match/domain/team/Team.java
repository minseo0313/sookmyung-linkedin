package com.sookmyung.campus_match.domain.team;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.CreatedFrom;
import com.sookmyung.campus_match.domain.post.Post;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team extends BaseEntity {

    @Column(name = "team_name", nullable = false, length = 255)
    private String teamName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_from", nullable = false)
    private CreatedFrom createdFrom;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "max_members")
    private Integer maxMembers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TeamSchedule> schedules = new ArrayList<>();

    // 도메인 메서드들
    public void confirm() {
        this.confirmedAt = LocalDateTime.now();
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isConfirmed() {
        return this.confirmedAt != null;
    }

    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }

    public boolean canAddMember() {
        return this.maxMembers == null || this.members.size() < this.maxMembers;
    }

    public void addMember(TeamMember member) {
        if (!canAddMember()) {
            throw new IllegalStateException("팀 인원이 가득 찼습니다.");
        }
        this.members.add(member);
    }

    public void removeMember(TeamMember member) {
        this.members.remove(member);
    }

    public boolean isMember(User user) {
        if (user == null) return false;
        return this.members.stream()
                .anyMatch(member -> member.getUser() != null && member.getUser().getId().equals(user.getId()));
    }

    public boolean isLeader(User user) {
        if (user == null || this.createdBy == null) return false;
        return this.createdBy.getId().equals(user.getId());
    }

    // 호환성 메서드들
    public String getName() {
        return this.teamName != null ? this.teamName : "Team-" + this.getId();
    }

    /**
     * 현재 팀 멤버 수를 반환 (지연 로딩된 members 리스트 기준)
     * 주의: JPA 지연 로딩으로 인해 members가 초기화되지 않은 경우 0을 반환할 수 있음
     * 정확한 멤버 수가 필요한 경우 TeamMemberRepository.countByTeam_Id() 사용 권장
     */
    public int getCurrentMemberCount() {
        return this.members != null ? this.members.size() : 0;
    }

    /**
     * 최대 멤버 수를 안전하게 반환
     */
    public Integer getMaxMembers() {
        return this.maxMembers;
    }

    /**
     * 팀 인원 제한이 있는지 확인
     */
    public boolean hasMemberLimit() {
        return this.maxMembers != null;
    }

    /**
     * 팀 인원이 가득 찼는지 확인 (안전한 버전)
     */
    public boolean isFull() {
        if (this.maxMembers == null) return false;
        return this.getCurrentMemberCount() >= this.maxMembers;
    }
}
