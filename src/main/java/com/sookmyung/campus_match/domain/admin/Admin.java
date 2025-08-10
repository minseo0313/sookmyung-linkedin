package com.sookmyung.campus_match.domain.admin;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자 엔티티
 * - 시스템 공지, 회원 승인/탈퇴, 게시글 관리 등 권한을 가짐
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admins")
public class Admin extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 관리자 계정 아이디

    @Column(nullable = false)
    private String password; // 비밀번호 (암호화 저장)

    @Column(nullable = false, length = 100)
    private String name; // 관리자 이름

    @Column(length = 100)
    private String email; // 관리자 이메일 (선택)
}
