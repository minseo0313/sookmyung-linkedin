package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dev 환경용 스케줄 스텁 컨트롤러
 * 실제 스케줄 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Profile("dev")
public class DevScheduleController {

    private final CurrentUserResolver currentUserResolver;
    
    // In-memory 캐시
    private final ConcurrentHashMap<Long, ScheduleData> schedules = new ConcurrentHashMap<>();
    private final AtomicLong scheduleIdCounter = new AtomicLong(1);

    @GetMapping
    public ResponseEntity<ApiEnvelope<List<ScheduleData>>> getSchedules() {
        log.info("Dev 환경 - 스케줄 목록 스텁 호출");
        
        List<ScheduleData> scheduleList = schedules.values().stream().toList();
        
        return ResponseEntity.ok(ApiEnvelope.<List<ScheduleData>>builder()
                .success(true)
                .code("OK")
                .message("Dev 환경: 스케줄 목록")
                .data(scheduleList)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<ScheduleData>> getSchedule(@PathVariable Long id) {
        log.info("Dev 환경 - 스케줄 상세 스텁 호출: {}", id);
        
        ScheduleData schedule = schedules.get(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(ApiEnvelope.<ScheduleData>builder()
                .success(true)
                .code("OK")
                .message("Dev 환경: 스케줄 상세")
                .data(schedule)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiEnvelope<ScheduleData>> createSchedule(@RequestBody ScheduleRequest request) {
        log.info("Dev 환경 - 스케줄 생성 스텁 호출: {}", request.title);
        
        Long scheduleId = scheduleIdCounter.getAndIncrement();
        ScheduleData schedule = new ScheduleData(
                scheduleId,
                request.title,
                request.description,
                request.startDate,
                request.endDate,
                currentUserResolver.currentUserId()
        );
        
        schedules.put(scheduleId, schedule);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/schedules/" + scheduleId)
                .body(ApiEnvelope.<ScheduleData>builder()
                        .success(true)
                        .code("CREATED")
                        .message("Dev 환경: 스케줄 생성 완료")
                        .data(schedule)
                        .build());
    }

    // 내부 DTO 클래스들
    public static class ScheduleData {
        public final Long id;
        public final String title;
        public final String description;
        public final String startDate;
        public final String endDate;
        public final Long ownerId;

        public ScheduleData(Long id, String title, String description, String startDate, String endDate, Long ownerId) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.ownerId = ownerId;
        }
    }

    public static class ScheduleRequest {
        public String title;
        public String description;
        public String startDate;
        public String endDate;
    }
}

