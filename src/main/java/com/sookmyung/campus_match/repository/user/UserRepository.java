package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.user.enum_.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // === 단건 조회 (로그인/프로필 등) ===
    Optional<User> findByUsername(String username);          // 아이디(=학번) 기반
    Optional<User> findByStudentId(String studentId);        // 필요 시 별도 사용
    Optional<User> findBySookmyungEmail(String sookmyungEmail);
    Optional<User> findByPhone(String phone);

    // === 중복 체크 ===
    boolean existsByUsername(String username);
    boolean existsByStudentId(String studentId);
    boolean existsBySookmyungEmail(String sookmyungEmail);
    boolean existsByPhone(String phone);

    // === 상태/목록 ===
    List<User> findByApprovalStatus(ApprovalStatus status);
    long countByApprovalStatus(ApprovalStatus status);

    // === 간단 검색 (이름/아이디 부분 일치 + 페이징) ===
    Page<User> findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String nameKeyword, String usernameKeyword, Pageable pageable
    );
}
