package com.sookmyung.campus_match.repository.admin;

import com.sookmyung.campus_match.domain.admin.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 로그인/단건 조회
    Optional<Admin> findByUsername(String username);

    // 중복 체크
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // 이름 검색 + 페이징
    Page<Admin> findByNameContainingIgnoreCase(String nameKeyword, Pageable pageable);

    // 최근 생성 순 상위 N (예: 리스트 위젯)
    List<Admin> findTop10ByOrderByCreatedAtDesc();
}
