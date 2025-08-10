package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.enum_.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_users_student_id", columnNames = {"studentId"}),
                @UniqueConstraint(name = "uk_users_email", columnNames = {"sookmyungEmail"})
        },
        indexes = {
                @Index(name = "idx_users_approval_status", columnList = "approvalStatus"),
                @Index(name = "idx_users_department", columnList = "department")
        }
)
public class User extends BaseEntity {

    /**
     * 로그인 식별자 (= 학번을 username으로 사용)
     * - 프론트/백엔드 모두 username 필드로 통일
     */
    @Column(nullable = false, length = 20)
    private String username;

    /**
     * 암호화(해시)된 비밀번호
     * - 평문 저장 금지; 서비스에서 인코딩 후 세터/업데이터로만 반영
     */
    @Column(nullable = false, length = 255)
    private String passwordHash;

    /** 실명 */
    @Column(nullable = false, length = 50)
    private String fullName;

    /** 생년월일 */
    @Column(nullable = false)
    private LocalDate birthDate;

    /** 연락처 */
    @Column(nullable = false, length = 20)
    private String phone;

    /** 학번 (username와 동일 값 보관. 검색/표시 용도로 분리 유지) */
    @Column(nullable = false, length = 20)
    private String studentId;

    /** 학과/소속 */
    @Column(nullable = false, length = 100)
    private String department;

    /** 숙명 이메일 */
    @Column(nullable = false, length = 120)
    private String sookmyungEmail;

    /**
     * 승인 상태 (배지)
     * - PENDING: 학생 A (읽기 제한/쓰기 불가)
     * - APPROVED: 학생 B (전체 권한)
     * - REJECTED: 가입 거절(로그인/접근 정책 별도)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /**
     * 운영진 여부 (팀 전역/시스템 전역 중 택1 설계)
     * - 시스템 전역 운영진 플래그가 필요할 때 사용
     * - 팀 단위 권한은 TeamMember.role로 별도 관리
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean operator = false;

    /**
     * 신고 누적 횟수 (관리자 제재 기준: 5회 이상 등)
     */
    @Column(nullable = false)
    @Builder.Default
    private int reportCount = 0;

    /* ==========================
       도메인 메서드 (상태 변경)
       ========================== */

    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    public void reject() {
        this.approvalStatus = ApprovalStatus.REJECTED;
    }

    public void markPending() {
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    /** 신고 누적 증가 (하한 0 보장) */
    public void increaseReportCount(int by) {
        if (by <= 0) return;
        this.reportCount += by;
    }

    /** 비밀번호 해시 교체 (서비스 레이어에서 인코딩 후 호출) */
    public void changePasswordHash(String encodedPassword) {
        this.passwordHash = encodedPassword;
    }

    /** 기본 정보 변경(마이페이지/관리자용) */
    public void updateProfileBasics(String fullName, LocalDate birthDate, String phone,
                                    String department, String sookmyungEmail) {
        if (fullName != null) this.fullName = fullName;
        if (birthDate != null) this.birthDate = birthDate;
        if (phone != null) this.phone = phone;
        if (department != null) this.department = department;
        if (sookmyungEmail != null) this.sookmyungEmail = sookmyungEmail;
    }
}
