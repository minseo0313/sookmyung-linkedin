package com.sookmyung.campus_match.domain.user.enum_;

/**
 * 회원 승인 상태 (배지와 1:1 매핑)
 * - PENDING: 가입은 되었지만 관리자 승인이 안 된 상태 → 학생 A
 * - APPROVED: 관리자 승인 완료 → 학생 B (배지 보유, 전체 권한)
 * - REJECTED: 관리자에 의해 가입 거절 처리됨
 */
public enum ApprovalStatus {
    PENDING,    // 승인 대기 (기본값)
    APPROVED,   // 승인 완료
    REJECTED    // 승인 거절
}
