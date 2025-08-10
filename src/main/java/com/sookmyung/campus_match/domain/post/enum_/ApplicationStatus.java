package com.sookmyung.campus_match.domain.post.enum_;

/**
 * 게시글 참여 신청 상태
 * - PENDING: 신청 대기 (기본값)
 * - ACCEPTED: 신청 수락 → 팀 매칭 확정 시 팀원으로 편입
 * - REJECTED: 신청 거절
 */
public enum ApplicationStatus {
    PENDING,   // 신청 대기
    ACCEPTED,  // 수락됨
    REJECTED   // 거절됨
}
