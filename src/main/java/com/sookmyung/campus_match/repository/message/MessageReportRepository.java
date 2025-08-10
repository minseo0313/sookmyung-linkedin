package com.sookmyung.campus_match.repository.message;

import com.sookmyung.campus_match.domain.message.MessageReport;
import com.sookmyung.campus_match.domain.message.enum_.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {

    // 특정 메시지의 모든 신고 내역
    List<MessageReport> findByMessage_Id(Long messageId);

    // 신고 상태별 조회 (관리자 페이지)
    Page<MessageReport> findByStatus(ReportStatus status, Pageable pageable);

    // 특정 유저가 한 신고 내역
    List<MessageReport> findByReporter_Id(Long reporterId);

    // 특정 메시지에 대해 해당 유저가 이미 신고했는지 여부
    boolean existsByReporter_IdAndMessage_Id(Long reporterId, Long messageId);

    // 메시지 + 상태로 단건 조회
    Optional<MessageReport> findByMessage_IdAndStatus(Long messageId, ReportStatus status);

    // 전체 개수(상태별)
    long countByStatus(ReportStatus status);
}
