package com.sookmyung.campus_match.repository.admin;

import com.sookmyung.campus_match.domain.admin.SystemNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemNoticeRepository extends JpaRepository<SystemNotice, Long> {

    List<SystemNotice> findByCreatedById(Long createdById);
    
    @Query("SELECT sn FROM SystemNotice sn WHERE " +
           "(:visibleFrom IS NULL OR sn.visibleFrom <= :now) AND " +
           "(:visibleTo IS NULL OR sn.visibleTo >= :now)")
    List<SystemNotice> findVisibleNotices(@Param("now") LocalDateTime now);
    
    @Query("SELECT sn FROM SystemNotice sn WHERE " +
           "(:visibleFrom IS NULL OR sn.visibleFrom <= :now1) AND " +
           "(:visibleTo IS NULL OR sn.visibleTo >= :now2)")
    Page<SystemNotice> findByVisibleFromBeforeAndVisibleToAfter(@Param("now1") LocalDateTime now1,
                                                                @Param("now2") LocalDateTime now2,
                                                                Pageable pageable);
    
    List<SystemNotice> findByOrderByCreatedAtDesc();

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Query("SELECT sn FROM SystemNotice sn WHERE sn.noticeTitle LIKE %:keyword%")
    Page<SystemNotice> findByTitleContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT sn FROM SystemNotice sn ORDER BY sn.createdAt DESC")
    List<SystemNotice> findTop5ByOrderByCreatedAtDesc();
    
    @Query("SELECT sn FROM SystemNotice sn WHERE sn.createdBy.adminName LIKE %:adminUsername%")
    Page<SystemNotice> findByAdminUsernameContainingIgnoreCase(@Param("adminUsername") String adminUsername, Pageable pageable);
    
    long count();
}
