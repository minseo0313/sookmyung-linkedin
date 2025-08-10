package com.sookmyung.campus_match.controller.admin;

import com.sookmyung.campus_match.dto.admin.ApproveUserRequest;
import com.sookmyung.campus_match.dto.admin.StatisticsResponse;
import com.sookmyung.campus_match.dto.admin.SystemNoticeRequest;
import com.sookmyung.campus_match.dto.admin.SystemNoticeResponse;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.message.MessageReportResponse;
import com.sookmyung.campus_match.service.admin.AdminService;
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

@Tag(name = "관리자", description = "관리자 기능 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "전체 회원 목록 조회", description = "승인/탈퇴 대상 확인용 회원 목록을 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<Object>>> getAllUsers(Pageable pageable) {
        // TODO: 실제 회원 목록 조회 로직 구현 필요
        Page<Object> users = Page.empty(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "회원 승인", description = "가입 승인 처리를 합니다.")
    @PatchMapping("/users/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ApproveUserRequest request) {
        
        adminService.approveUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("사용자가 승인되었습니다."));
    }

    @Operation(summary = "회원 강제 탈퇴", description = "신고 누적 회원 탈퇴 처리를 합니다.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id) {
        
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("사용자가 탈퇴 처리되었습니다."));
    }

    @Operation(summary = "게시글 삭제", description = "이상 게시글을 삭제합니다.")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id) {
        
        adminService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }

    @Operation(summary = "전체 공지 등록", description = "시스템 점검 등 공지사항을 등록합니다.")
    @PostMapping("/notices")
    public ResponseEntity<ApiResponse<SystemNoticeResponse>> createNotice(
            @Valid @RequestBody SystemNoticeRequest request) {
        
        SystemNoticeResponse notice = adminService.createNotice(request);
        return ResponseEntity.ok(ApiResponse.success(notice));
    }

    @Operation(summary = "공지 상세 조회", description = "공지사항 상세 정보를 조회합니다.")
    @GetMapping("/notices/{id}")
    public ResponseEntity<ApiResponse<SystemNoticeResponse>> getNotice(
            @Parameter(description = "공지 ID", example = "1")
            @PathVariable Long id) {
        
        SystemNoticeResponse notice = adminService.getNotice(id);
        return ResponseEntity.ok(ApiResponse.success(notice));
    }

    @Operation(summary = "신고 메시지 확인", description = "신고된 메시지 리스트를 조회합니다.")
    @GetMapping("/reports/messages")
    public ResponseEntity<ApiResponse<List<MessageReportResponse>>> getReportedMessages() {
        List<MessageReportResponse> reports = adminService.getReportedMessages();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @Operation(summary = "시스템 통계", description = "방문자, 회원 수 등 통계를 확인합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getStatistics() {
        StatisticsResponse statistics = adminService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}
