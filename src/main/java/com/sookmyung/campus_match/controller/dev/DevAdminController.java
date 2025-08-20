package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dev 환경용 관리자 API 스텁 컨트롤러
 * 실제 관리자 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Profile("dev")
public class DevAdminController {

    private final CurrentUserResolver currentUserResolver;
    
    // In-memory 캐시
    private final ConcurrentHashMap<Long, NoticeData> notices = new ConcurrentHashMap<>();
    private final AtomicLong noticeIdCounter = new AtomicLong(1);

    // 기존 AdminController와 충돌하는 getUsers 메서드 제거
    // 실제 구현된 AdminController의 getAllUsers 메서드가 우선 사용됨

    // 기존 AdminController와 충돌하는 getStatistics 메서드 제거
    // 실제 구현된 AdminController의 getStatistics 메서드가 우선 사용됨

    @GetMapping("/notices")
    public ResponseEntity<ApiEnvelope<List<NoticeData>>> getNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Dev 환경 - 시스템 공지 목록 스텁 호출 (page: {}, size: {})", page, size);
        
        List<NoticeData> noticeList = notices.values().stream().toList();
        
        return ResponseEntity.ok(ApiEnvelope.<List<NoticeData>>builder()
                .success(true)
                .code("OK")
                .message("Dev 환경: 시스템 공지 목록")
                .data(noticeList)
                .build());
    }

    // 기존 AdminController와 충돌하는 createNotice 메서드 제거
    // 실제 구현된 AdminController의 createNotice 메서드가 우선 사용됨

    // 내부 DTO 클래스들
    public static class UserData {
        public final Long id;
        public final String username;
        public final String email;
        public final String role;

        public UserData(Long id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }
    }

    public static class StatisticsData {
        public final Long totalUsers;
        public final Long totalTeams;
        public final Long totalPosts;
        public final Long totalMessages;

        public StatisticsData(Long totalUsers, Long totalTeams, Long totalPosts, Long totalMessages) {
            this.totalUsers = totalUsers;
            this.totalTeams = totalTeams;
            this.totalPosts = totalPosts;
            this.totalMessages = totalMessages;
        }
    }

    public static class NoticeData {
        public final Long id;
        public final String title;
        public final String body;
        public final Instant createdAt;
        public final boolean pinned;

        public NoticeData(Long id, String title, String body, Instant createdAt, boolean pinned) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.createdAt = createdAt;
            this.pinned = pinned;
        }
    }

    public static class NoticeRequest {
        public String title;
        public String content;
    }
}
