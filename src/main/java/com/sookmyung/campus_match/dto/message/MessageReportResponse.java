package com.sookmyung.campus_match.dto.message;

import com.sookmyung.campus_match.domain.message.MessageReport;
import com.sookmyung.campus_match.domain.common.enums.ApplicationStatus;
import com.sookmyung.campus_match.domain.common.enums.MessageReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 메시지 신고 응답 DTO
 * - 신고된 메시지의 정보 및 상태를 반환
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageReportResponse {

    @Schema(description = "신고 ID", example = "10")
    private Long id;

    @Schema(description = "신고된 메시지 ID", example = "42")
    private Long messageId;

    @Schema(description = "신고 사유", example = "SPAM")
    private MessageReportReason reason;

    @Schema(description = "신고 처리 상태", example = "PENDING")
    private ApplicationStatus status;

    @Schema(description = "신고 일시", example = "2025-08-10T14:30:00")
    private LocalDateTime reportedAt;

    public static MessageReportResponse from(MessageReport report) {
        return MessageReportResponse.builder()
                .id(report.getId())
                .messageId(report.getMessage() != null ? report.getMessage().getId() : 
                          (report.getReportedMessage() != null ? report.getReportedMessage().getId() : null))
                .reason(report.getReason())
                .status(report.getReportStatus())
                .reportedAt(report.getReportedAt())
                .build();
    }
}
