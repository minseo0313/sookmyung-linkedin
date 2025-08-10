package com.sookmyung.campus_match.domain.team.enum_;

/**
 * 팀 내 멤버 역할
 * - LEADER: 팀장 (write 및 modify 권한)
 * - MEMBER: 팀원 (read 권한)
 */
public enum MemberRole {
    LEADER,
    MEMBER
}
