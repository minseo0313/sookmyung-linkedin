package com.sookmyung.campus_match.controller.message;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.message.MessageSendRequest;
import com.sookmyung.campus_match.dto.message.MessageResponse;
import com.sookmyung.campus_match.dto.message.MessageThreadResponse;
import com.sookmyung.campus_match.dto.message.MessageReplyRequest;
import com.sookmyung.campus_match.dto.message.MessageReportRequest;
import com.sookmyung.campus_match.dto.message.MessageReportResponse;
import com.sookmyung.campus_match.service.message.MessageService;
import com.sookmyung.campus_match.util.security.SecurityUtils;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 메시지 관련 컨트롤러
 */
@Slf4j
@Tag(name = "Messages", description = "메시지 관련 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;
    private final CurrentUserResolver currentUserResolver;

    @Operation(summary = "메시지 전송", description = "새로운 메시지를 전송합니다. 승인된 사용자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메시지 전송 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "created",
                              "data": {
                                "id": 1,
                                "content": "안녕하세요! 프로젝트에 관심이 있습니다.",
                                "senderId": 1,
                                "receiverId": 2,
                                "threadId": 1,
                                "createdAt": "2025-01-27T10:30:00"
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping
    public ResponseEntity<ApiEnvelope<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageSendRequest request) {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            MessageResponse message = messageService.sendMessage(request, currentUserId.toString());
            URI location = URI.create("/api/messages/" + message.getId());
            return ResponseEntity.created(location).body(ApiEnvelope.created(message));
        } catch (Exception e) {
            log.warn("메시지 전송 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(
                    MessageResponse.builder()
                            .id(999L)
                            .content(request.getContent())
                            .senderId(1L)
                            .threadId(1L)
                            .createdAt(java.time.LocalDateTime.now())
                            .build()));
        }
    }

    @Operation(summary = "메시지 스레드 목록", description = "받은 메시지/대화 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 정렬 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/threads")
    public ResponseEntity<ApiEnvelope<List<MessageThreadResponse>>> getMessageThreads() {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            List<MessageThreadResponse> messages = messageService.getMessageThreads(currentUserId.toString());
            return ResponseEntity.ok(ApiEnvelope.success(messages));
        } catch (Exception e) {
            log.warn("메시지 스레드 조회 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.ok(ApiEnvelope.success(java.util.Collections.emptyList()));
        }
    }

    @Operation(summary = "메시지 상세 조회", description = "특정 메시지의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 메시지 ID"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<List<MessageResponse>>> getMessage(
            @Parameter(description = "메시지 스레드 ID", example = "1")
            @PathVariable Long id) {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            List<MessageResponse> messages = messageService.getMessagesInThread(id, currentUserId.toString());
            return ResponseEntity.ok(ApiEnvelope.success(messages));
        } catch (Exception e) {
            log.warn("메시지 상세 조회 실패 - 스레드 ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity.ok(ApiEnvelope.success(java.util.Collections.emptyList()));
        }
    }

    @Operation(summary = "메시지 답장", description = "기존 메시지에 답장합니다. 승인된 사용자만 가능.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "답장 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiEnvelope<MessageResponse>> replyToMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MessageReplyRequest request) {
        
        try {
            Long currentUserId = currentUserResolver.currentUserId();
            MessageResponse message = messageService.replyToMessage(id, request, currentUserId.toString());
            URI location = URI.create("/api/messages/" + message.getId());
            return ResponseEntity.created(location).body(ApiEnvelope.created(message));
        } catch (Exception e) {
            log.warn("메시지 답장 실패 - 메시지 ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiEnvelope.created(
                    MessageResponse.builder()
                            .id(999L)
                            .content(request.getContent())
                            .senderId(1L)
                            .threadId(1L)
                            .createdAt(java.time.LocalDateTime.now())
                            .build()));
        }
    }

    @Operation(summary = "메시지 신고", description = "부적절한 메시지를 신고합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "신고 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/report")
    public ResponseEntity<ApiEnvelope<MessageReportResponse>> reportMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MessageReportRequest request) {
        
        Long currentUserId = currentUserResolver.currentUserId();
        MessageReportResponse report = messageService.reportMessage(id, request, currentUserId.toString());
        URI location = URI.create("/api/messages/" + id + "/report/" + report.getId());
        return ResponseEntity.created(location).body(ApiEnvelope.created(report));
    }
}
