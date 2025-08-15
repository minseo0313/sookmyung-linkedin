package com.sookmyung.campus_match.service.admin;

import com.sookmyung.campus_match.domain.admin.SystemNotice;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.dto.admin.ApproveUserRequest;
import com.sookmyung.campus_match.dto.admin.StatisticsResponse;
import com.sookmyung.campus_match.dto.admin.SystemNoticeRequest;
import com.sookmyung.campus_match.dto.admin.SystemNoticeResponse;
import com.sookmyung.campus_match.dto.message.MessageReportResponse;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.admin.SystemNoticeRepository;
import com.sookmyung.campus_match.repository.message.MessageReportRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.sookmyung.campus_match.domain.admin.Admin;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final SystemNoticeRepository systemNoticeRepository;
    private final MessageReportRepository messageReportRepository;

    @Transactional
    public void approveUser(Long userId, ApproveUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        user.approve();
        userRepository.save(user);
        
        log.info("사용자 승인: 사용자 ID {}", userId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 신고 횟수가 5회 이상인지 확인
        if (user.getReportCount() < 5) {
            throw new ApiException(ErrorCode.FORBIDDEN, "신고 횟수가 5회 미만인 사용자는 삭제할 수 없습니다.");
        }

        userRepository.delete(user);
        log.info("사용자 강제 삭제: 사용자 ID {}, 신고 횟수 {}", userId, user.getReportCount());
    }

    @Transactional
    public void deletePost(Long postId) {
        // TODO: 게시글 삭제 로직 구현 필요
        log.info("게시글 삭제: 게시글 ID {}", postId);
    }

    @Transactional
    public SystemNoticeResponse createNotice(SystemNoticeRequest request, String adminUsername) {
        // TODO: 실제로는 SecurityContext에서 현재 로그인한 관리자 정보를 가져와야 함
        // 임시로 null로 설정 (실제 구현 시 AdminRepository에서 조회)
        Admin admin = null; // TODO: AdminRepository에서 adminUsername으로 조회
        
        SystemNotice notice = SystemNotice.builder()
                .createdBy(admin)
                .noticeTitle(request.getTitle())
                .noticeContent(request.getContent())
                .build();

        SystemNotice savedNotice = systemNoticeRepository.save(notice);
        return SystemNoticeResponse.from(savedNotice);
    }

    public SystemNoticeResponse getNotice(Long noticeId) {
        SystemNotice notice = systemNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "공지사항을 찾을 수 없습니다."));

        return SystemNoticeResponse.from(notice);
    }

    public List<MessageReportResponse> getReportedMessages() {
        // TODO: 신고된 메시지 조회 로직 구현 필요
        return List.of(); // 임시 빈 리스트
    }

    public StatisticsResponse getStatistics() {
        long totalUsers = userRepository.count();
        long approvedUsers = userRepository.countByApprovalStatus(ApprovalStatus.APPROVED);
        long pendingUsers = userRepository.countByApprovalStatus(ApprovalStatus.PENDING);
        
        // TODO: 게시글 수, 방문자 수 등 추가 통계 구현 필요
        
        return StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .approvedUsers(approvedUsers)
                .pendingUsers(pendingUsers)
                .totalPosts(0L) // TODO: 실제 게시글 수로 변경
                .dailyVisitors(0L) // TODO: 실제 방문자 수로 변경
                .build();
    }
}
