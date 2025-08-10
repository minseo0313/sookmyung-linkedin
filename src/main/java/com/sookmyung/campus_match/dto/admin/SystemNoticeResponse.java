package com.sookmyung.campus_match.dto.admin;

import com.sookmyung.campus_match.domain.admin.SystemNotice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 시스템 공지 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SystemNoticeResponse {

    @Schema(description = "공지 ID", example = "5")
    private Long id;

    @Schema(description = "공지 제목", example = "시스템 점검 안내")
    private String title;

    @Schema(description = "공지 내용", example = "8월 15일 00:00~06:00 서버 점검이 예정되어 있습니다.")
    private String content;

    @Schema(description = "공지 활성화 여부", example = "true")
    private boolean active;

    @Schema(description = "공지 생성일", example = "2025-08-10T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "공지 수정일", example = "2025-08-10T16:00:00")
    private LocalDateTime updatedAt;

    public static SystemNoticeResponse from(SystemNotice notice) {
        return SystemNoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .active(notice.isActive())
                .createdAt(notice.getCreatedAt() != null ? 
                        LocalDateTime.ofInstant(Instant.from(notice.getCreatedAt()), java.time.ZoneId.systemDefault()) : null)
                .updatedAt(notice.getUpdatedAt() != null ? 
                        LocalDateTime.ofInstant(Instant.from(notice.getUpdatedAt()), java.time.ZoneId.systemDefault()) : null)
                .build();
    }
}
