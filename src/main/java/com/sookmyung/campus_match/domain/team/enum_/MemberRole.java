package com.sookmyung.campus_match.domain.team.enum_;

/**
 * 팀 내 멤버 역할
 * - OWNER: 팀장 (write 및 modify 권한)
 * - OPERATOR: 운영진 (write 및 modify 권한)
 * - MEMBER: 팀원 (read 권한)
 */
public enum MemberRole {
    OWNER,
    OPERATOR,
    MEMBER
}
