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
 * Dev 환경용 메시지 스텁 컨트롤러
 * 실제 메시지 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Profile("dev")
public class DevMessageController {

    private final CurrentUserResolver currentUserResolver;
    
    // In-memory 캐시
    private final ConcurrentHashMap<Long, ThreadData> threads = new ConcurrentHashMap<>();
    private final AtomicLong threadIdCounter = new AtomicLong(1);
    private final AtomicLong messageIdCounter = new AtomicLong(1);

    // 기존 MessageController와 충돌하는 getThreads 메서드 제거
    // 실제 구현된 MessageController의 getMessageThreads 메서드가 우선 사용됨

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ApiEnvelope<ThreadDetail>> getThread(@PathVariable Long threadId) {
        log.info("Dev 환경 - 메시지 스레드 상세 스텁 호출: {}", threadId);
        
        ThreadData thread = threads.get(threadId);
        if (thread == null) {
            // 스레드가 없으면 새로 생성
            thread = new ThreadData(threadId, List.of(currentUserResolver.currentUserId()));
            threads.put(threadId, thread);
        }
        
        ThreadDetail detail = new ThreadDetail(threadId, thread.messages);
        
        return ResponseEntity.ok(ApiEnvelope.<ThreadDetail>builder()
                .success(true)
                .code("OK")
                .message("Dev 환경: 메시지 스레드 상세")
                .data(detail)
                .build());
    }

    @PostMapping("/threads/{threadId}")
    public ResponseEntity<ApiEnvelope<MessageData>> sendMessage(
            @PathVariable Long threadId,
            @RequestBody MessageRequest request) {
        log.info("Dev 환경 - 메시지 전송 스텁 호출: {}", threadId);
        
        ThreadData thread = threads.computeIfAbsent(threadId, 
                id -> new ThreadData(id, List.of(currentUserResolver.currentUserId())));
        
        MessageData message = new MessageData(
                messageIdCounter.getAndIncrement(),
                threadId,
                currentUserResolver.currentUserId(),
                thread.participants.stream().filter(id -> !id.equals(currentUserResolver.currentUserId())).findFirst().orElse(1L),
                request.content,
                Instant.now()
        );
        
        thread.messages.add(message);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiEnvelope.<MessageData>builder()
                        .success(true)
                        .code("CREATED")
                        .message("Dev 환경: 메시지 전송 완료")
                        .data(message)
                        .build());
    }



    // 내부 DTO 클래스들
    public static class ThreadSummary {
        public final Long threadId;
        public final String lastMessage;
        public final Instant lastAt;
        public final List<Long> participants;

        public ThreadSummary(Long threadId, String lastMessage, Instant lastAt, List<Long> participants) {
            this.threadId = threadId;
            this.lastMessage = lastMessage;
            this.lastAt = lastAt;
            this.participants = participants;
        }
    }

    public static class ThreadDetail {
        public final Long threadId;
        public final List<MessageData> messages;

        public ThreadDetail(Long threadId, List<MessageData> messages) {
            this.threadId = threadId;
            this.messages = messages;
        }
    }

    public static class MessageData {
        public final Long messageId;
        public final Long threadId;
        public final Long fromUserId;
        public final Long toUserId;
        public final String body;
        public final Instant sentAt;

        public MessageData(Long messageId, Long threadId, Long fromUserId, Long toUserId, String body, Instant sentAt) {
            this.messageId = messageId;
            this.threadId = threadId;
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.body = body;
            this.sentAt = sentAt;
        }
    }

    public static class MessageRequest {
        public String content;
    }



    private static class ThreadData {
        public final Long threadId;
        public final List<Long> participants;
        public final List<MessageData> messages = new java.util.ArrayList<>();

        public ThreadData(Long threadId, List<Long> participants) {
            this.threadId = threadId;
            this.participants = participants;
        }
    }
}
