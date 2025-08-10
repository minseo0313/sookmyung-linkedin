package com.sookmyung.campus_match.controller.message;

import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.message.MessageReplyRequest;
import com.sookmyung.campus_match.dto.message.MessageReportRequest;
import com.sookmyung.campus_match.dto.message.MessageResponse;
import com.sookmyung.campus_match.dto.message.MessageSendRequest;
import com.sookmyung.campus_match.dto.message.MessageThreadResponse;
import com.sookmyung.campus_match.service.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "메시지 전송", description = "특정 사용자에게 첫 메시지를 전송합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageSendRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageResponse message = messageService.sendMessage(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(summary = "메시지 목록", description = "받은 메시지 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageThreadResponse>>> getMessageList(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<MessageThreadResponse> messages = messageService.getMessageList(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @Operation(summary = "메시지 신고", description = "부적절한 메시지를 신고합니다.")
    @PostMapping("/{id}/report")
    public ResponseEntity<ApiResponse<String>> reportMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MessageReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        messageService.reportMessage(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("메시지가 신고되었습니다."));
    }

    @Operation(summary = "메시지 상세 조회", description = "특정 메시지의 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageDetail(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageResponse message = messageService.getMessageDetail(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(summary = "메시지 답장", description = "기존 메시지에 답장을 보냅니다.")
    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<MessageResponse>> replyToMessage(
            @Parameter(description = "메시지 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MessageReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MessageResponse message = messageService.replyToMessage(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}
