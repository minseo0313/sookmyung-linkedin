package com.sookmyung.campus_match.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"passwordHash"})
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

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 추가 필드들 (기존 서비스 코드와 호환성을 위해)
    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "sookmyung_email", length = 255)
    private String sookmyungEmail;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "operator", nullable = false)
    @Builder.Default
    private Boolean operator = false;

    @Column(name = "report_count", nullable = false, columnDefinition = "int default 0")
    @Builder.Default
    private Integer reportCount = 0;

    @PrePersist
    void prePersist() {
        if (reportCount == null) reportCount = 0;
    }

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

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void increaseReportCount(int by) {
        if (by <= 0) return;
        this.reportCount += by;
    }

    public Integer getReportCount() {
        return this.reportCount;
    }

    public void changePasswordHash(String encodedPassword) {
        this.passwordHash = encodedPassword;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    // 빌더 메서드 추가
    public static class UserBuilder {
        public UserBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }
    }

    public String getUsername() {
        return this.username != null ? this.username : this.studentId;
    }

    public String getFullName() {
        return this.fullName != null ? this.fullName : this.name;
    }

    public String getName() {
        return this.name != null ? this.name : this.fullName;
    }

    public String getSookmyungEmail() {
        return this.sookmyungEmail != null ? this.sookmyungEmail : this.email;
    }

    public String getPhone() {
        return this.phone != null ? this.phone : this.phoneNumber;
    }
}
