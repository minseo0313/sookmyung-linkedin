package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "student_id", unique = true, nullable = false, length = 255)
    private String studentId;

    @Column(name = "department", nullable = false, length = 255)
    private String department;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", nullable = false, length = 255)
    private String phoneNumber;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "sookmyung_email", length = 255)
    private String sookmyungEmail;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "operator")
    private Boolean operator;

    @Column(name = "report_count")
    private Integer reportCount;

    // 추가 관계들 (기존 서비스 코드와 호환성을 위해)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Profile profile;

    // 도메인 메서드들
    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    public void reject() {
        this.approvalStatus = ApprovalStatus.REJECTED;
    }

    public void markPending() {
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    public boolean isApproved() {
        return this.approvalStatus == ApprovalStatus.APPROVED;
    }

    public boolean isPending() {
        return this.approvalStatus == ApprovalStatus.PENDING;
    }

    public boolean isRejected() {
        return this.approvalStatus == ApprovalStatus.REJECTED;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public boolean isOperator() {
        return this.operator != null && this.operator;
    }

    public void increaseReportCount(int by) {
        if (by <= 0) return;
        this.reportCount = (this.reportCount == null ? by : this.reportCount + by);
    }

    public int getReportCount() {
        return this.reportCount != null ? this.reportCount : 0;
    }

    public void changePasswordHash(String encodedPassword) {
        this.password = encodedPassword;
    }

    public String getPasswordHash() {
        return this.password;
    }

    // 빌더 메서드 추가
    public static class UserBuilder {
        public UserBuilder passwordHash(String passwordHash) {
            this.password = passwordHash;
            return this;
        }
    }

    public String getUsername() {
        return this.username != null ? this.username : this.studentId;
    }

    public String getFullName() {
        return this.fullName != null ? this.fullName : this.name;
    }

    public String getSookmyungEmail() {
        return this.sookmyungEmail != null ? this.sookmyungEmail : this.email;
    }

    public String getPhone() {
        return this.phone != null ? this.phone : this.phoneNumber;
    }
}
