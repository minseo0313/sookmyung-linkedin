package com.sookmyung.campus_match.controller.admin;

import com.sookmyung.campus_match.dto.admin.ApproveUserRequest;
import com.sookmyung.campus_match.dto.admin.StatisticsResponse;
import com.sookmyung.campus_match.dto.admin.SystemNoticeRequest;
import com.sookmyung.campus_match.dto.admin.SystemNoticeResponse;
import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.message.MessageReportResponse;
import com.sookmyung.campus_match.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    public ResponseEntity<ApiEnvelope<Page<Object>>> getAllUsers(Pageable pageable) {
        // TODO: 실제 회원 목록 조회 로직 구현 필요
        Page<Object> users = Page.empty(pageable);
        return ResponseEntity.ok(ApiEnvelope.success(users));
    }

    @Operation(
        summary = "회원 승인/반려", 
        description = "가입 승인 또는 반려 처리를 합니다. approved=true는 승인, false는 반려를 의미합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "승인/반려 처리 성공 - 사용자 상태가 변경되었습니다"),
        @ApiResponse(responseCode = "400", description = "요청 검증 실패 - approved 필드가 누락되었습니다"),
        @ApiResponse(responseCode = "403", description = "관리자 권한 없음 - 관리자만 접근 가능합니다"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음 - 해당 ID의 사용자가 존재하지 않습니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 시스템 오류가 발생했습니다")
    })
    @PatchMapping("/users/{id}/approve")
    public ResponseEntity<ApiEnvelope<String>> approveUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ApproveUserRequest request) {
        
        adminService.approveUser(id, request);
        return ResponseEntity.ok(ApiEnvelope.success("사용자 상태가 변경되었습니다."));
    }

    @Operation(
        summary = "회원 권한 변경", 
        description = "사용자의 운영자 권한을 변경합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
        @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiEnvelope<String>> changeUserRole(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "운영자 권한 여부", example = "true")
            @RequestParam Boolean isOperator) {
        
        adminService.changeUserRole(id, isOperator);
        return ResponseEntity.ok(ApiEnvelope.success("사용자 권한이 변경되었습니다."));
    }

    @Operation(summary = "회원 강제 탈퇴", description = "신고 누적 회원 탈퇴 처리를 합니다.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiEnvelope<String>> deleteUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id) {
        
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiEnvelope.success("사용자가 탈퇴 처리되었습니다."));
    }

    @Operation(summary = "게시글 삭제", description = "이상 게시글을 삭제합니다.")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiEnvelope<String>> deletePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id) {
        
        adminService.deletePost(id);
        return ResponseEntity.ok(ApiEnvelope.success("게시글이 삭제되었습니다."));
    }

    @Operation(summary = "전체 공지 등록", description = "시스템 점검 등 공지사항을 등록합니다.")
    @PostMapping("/notices")
    public ResponseEntity<ApiEnvelope<SystemNoticeResponse>> createNotice(
            @Valid @RequestBody SystemNoticeRequest request) {
        
        // TODO: 실제로는 SecurityContext에서 현재 로그인한 관리자 정보를 가져와야 함
        String adminUsername = "admin"; // 임시 값
        SystemNoticeResponse notice = adminService.createNotice(request, adminUsername);
        return ResponseEntity.ok(ApiEnvelope.success(notice));
    }

    @Operation(summary = "공지 상세 조회", description = "공지사항 상세 정보를 조회합니다.")
    @GetMapping("/notices/{id}")
    public ResponseEntity<ApiEnvelope<SystemNoticeResponse>> getNotice(
            @Parameter(description = "공지 ID", example = "1")
            @PathVariable Long id) {
        
        SystemNoticeResponse notice = adminService.getNotice(id);
        return ResponseEntity.ok(ApiEnvelope.success(notice));
    }

    @Operation(summary = "신고 메시지 확인", description = "신고된 메시지 리스트를 조회합니다.")
    @GetMapping("/reports/messages")
    public ResponseEntity<ApiEnvelope<List<MessageReportResponse>>> getReportedMessages() {
        List<MessageReportResponse> reports = adminService.getReportedMessages();
        return ResponseEntity.ok(ApiEnvelope.success(reports));
    }

    @Operation(summary = "시스템 통계", description = "방문자, 회원 수 등 통계를 확인합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<ApiEnvelope<StatisticsResponse>> getStatistics() {
        StatisticsResponse statistics = adminService.getStatistics();
        return ResponseEntity.ok(ApiEnvelope.success(statistics));
    }
}
