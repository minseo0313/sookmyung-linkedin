package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.Interest;
import com.sookmyung.campus_match.domain.user.enum_.InterestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    // === 단건 조회 ===
    Optional<Interest> findByName(String name);
    Optional<Interest> findByNameIgnoreCase(String name);
    Optional<Interest> findByNameIgnoreCaseAndType(String name, InterestType type);

    // === 목록/필터 ===
    List<Interest> findByType(InterestType type);
    Page<Interest> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 자동완성(프리픽스 매칭) - 상위 10개만
    List<Interest> findTop10ByNameStartingWithIgnoreCase(String prefix);

    // === 중복 체크 ===
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndType(String name, InterestType type);
}
