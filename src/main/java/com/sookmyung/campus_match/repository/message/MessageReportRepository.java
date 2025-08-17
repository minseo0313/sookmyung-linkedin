package com.sookmyung.campus_match.repository.message;

import com.sookmyung.campus_match.domain.message.MessageReport;
import com.sookmyung.campus_match.domain.message.Message;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {

    List<MessageReport> findByReportedUserId(Long reportedUserId);
    
    List<MessageReport> findByReportStatus(ApplicationStatus status);
    
    @Query("SELECT mr FROM MessageReport mr WHERE " +
           "(:status IS NULL OR mr.reportStatus = :status) AND " +
           "(:reportedUserId IS NULL OR mr.reportedUser.id = :reportedUserId)")
    Page<MessageReport> findByStatusAndReportedUser(@Param("status") ApplicationStatus status,
                                                   @Param("reportedUserId") Long reportedUserId,
                                                   Pageable pageable);
    
    long countByReportStatus(ApplicationStatus status);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Query("SELECT mr FROM MessageReport mr WHERE mr.reportedMessage.id = :messageId")
    List<MessageReport> findByMessageId(@Param("messageId") Long messageId);
    
    @Query("SELECT mr FROM MessageReport mr WHERE mr.reportStatus = :status")
    Page<MessageReport> findByStatus(@Param("status") ApplicationStatus status, Pageable pageable);
    
    @Query("SELECT mr FROM MessageReport mr WHERE mr.reporter.id = :reporterId")
    List<MessageReport> findByReporterId(@Param("reporterId") Long reporterId);
    
    @Query("SELECT mr FROM MessageReport mr WHERE mr.reporter.id = :reporterId AND mr.reportedMessage.id = :messageId")
    boolean existsByReporterIdAndMessageId(@Param("reporterId") Long reporterId, @Param("messageId") Long messageId);

    // MessageService에서 호출하는 메서드들
    @Query("SELECT mr FROM MessageReport mr WHERE mr.message = :message AND mr.reporter = :reporter")
    boolean existsByMessageAndReporter(@Param("message") Message message, @Param("reporter") User reporter);
}
