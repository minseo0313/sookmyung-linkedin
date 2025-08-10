package com.sookmyung.campus_match.repository.admin;

import com.sookmyung.campus_match.domain.admin.SystemNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemNoticeRepository extends JpaRepository<SystemNotice, Long> {

    // 제목 키워드 검색 + 페이징
    Page<SystemNotice> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 최신 공지 상위 N개 (예: 메인 화면 노출)
    List<SystemNotice> findTop5ByOrderByCreatedAtDesc();

    // 작성자별 공지 목록
    Page<SystemNotice> findByCreatedByContainingIgnoreCase(String createdBy, Pageable pageable);

    // 전체 공지 개수
    long count();
}
