package com.sookmyung.campus_match.controller.message;

import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.message.MessageSendRequest;
import com.sookmyung.campus_match.dto.message.MessageResponse;
import com.sookmyung.campus_match.dto.message.MessageThreadResponse;
import com.sookmyung.campus_match.dto.message.MessageReplyRequest;
import com.sookmyung.campus_match.dto.message.MessageReportRequest;
import com.sookmyung.campus_match.dto.message.MessageReportResponse;
import com.sookmyung.campus_match.service.message.MessageService;
import com.sookmyung.campus_match.util.security.RequiresApproval;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "메시지", description = "메시지 관련 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "메시지 전송", description = "새로운 메시지를 전송합니다.")
    @PostMapping("/send")
    @RequiresApproval(message = "승인된 사용자만 메시지를 전송할 수 있습니다.")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageSendRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageResponse message = messageService.sendMessage(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.created(message));
    }

    @Operation(summary = "메시지 답장", description = "기존 메시지에 답장합니다.")
    @PostMapping("/threads/{threadId}/reply")
    @RequiresApproval(message = "승인된 사용자만 메시지에 답장할 수 있습니다.")
    public ResponseEntity<ApiResponse<MessageResponse>> replyToMessage(
            @Parameter(description = "메시지 스레드 ID", example = "1")
            @PathVariable Long threadId,
            @Valid @RequestBody MessageReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageResponse message = messageService.replyToMessage(threadId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.created(message));
    }

    @Operation(summary = "메시지 스레드 목록", description = "사용자의 메시지 스레드 목록을 조회합니다.")
    @GetMapping("/threads")
    public ResponseEntity<ApiResponse<List<MessageThreadResponse>>> getMessageThreads(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<MessageThreadResponse> threads = messageService.getMessageThreads(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(threads));
    }

    @Operation(summary = "스레드 메시지 목록", description = "특정 스레드의 메시지 목록을 조회합니다.")
    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesInThread(
            @Parameter(description = "메시지 스레드 ID", example = "1")
            @PathVariable Long threadId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<MessageResponse> messages = messageService.getMessagesInThread(threadId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @Operation(summary = "메시지 수정", description = "메시지를 수정합니다.")
    @PutMapping("/{messageId}")
    @RequiresApproval(message = "승인된 사용자만 메시지를 수정할 수 있습니다.")
    public ResponseEntity<ApiResponse<MessageResponse>> editMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long messageId,
            @Parameter(description = "새로운 내용", example = "수정된 메시지 내용입니다.")
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageResponse message = messageService.editMessage(messageId, content, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(summary = "메시지 삭제", description = "메시지를 삭제합니다.")
    @DeleteMapping("/{messageId}")
    @RequiresApproval(message = "승인된 사용자만 메시지를 삭제할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> deleteMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        messageService.deleteMessage(messageId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("메시지가 삭제되었습니다."));
    }

    @Operation(summary = "메시지 신고", description = "부적절한 메시지를 신고합니다.")
    @PostMapping("/{messageId}/report")
    @RequiresApproval(message = "승인된 사용자만 메시지를 신고할 수 있습니다.")
    public ResponseEntity<ApiResponse<MessageReportResponse>> reportMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long messageId,
            @Valid @RequestBody MessageReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageReportResponse report = messageService.reportMessage(messageId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.created(report));
    }

    @Operation(summary = "내 메시지 목록", description = "사용자가 보낸 메시지 목록을 페이징하여 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMyMessages(
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=createdAt,desc")
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Page<MessageResponse> messages = messageService.getUserMessages(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @Operation(summary = "읽지 않은 메시지 수", description = "읽지 않은 메시지 개수를 조회합니다.")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        long count = messageService.getUnreadMessageCount(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
