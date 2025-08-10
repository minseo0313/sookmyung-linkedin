package com.sookmyung.campus_match.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 시스템 통계 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StatisticsResponse {

    @Schema(description = "총 방문자 수", example = "12453")
    private long totalVisitors;

    @Schema(description = "총 회원 수", example = "352")
    private long totalUsers;

    @Schema(description = "오늘 가입한 회원 수", example = "5")
    private long newUsersToday;

    @Schema(description = "총 게시글 수", example = "1287")
    private long totalPosts;

    @Schema(description = "오늘 작성된 게시글 수", example = "12")
    private long newPostsToday;

    @Schema(description = "신고된 메시지 수", example = "7")
    private long reportedMessagesCount;
}
