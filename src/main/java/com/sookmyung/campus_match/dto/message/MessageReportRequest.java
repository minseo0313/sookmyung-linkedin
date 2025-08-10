package com.sookmyung.campus_match.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 메시지 신고 요청 DTO
 * - 특정 메시지에 대한 신고 사유를 전달
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageReportRequest {

    @Schema(description = "신고할 메시지 ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long messageId;

    @Schema(description = "신고 사유", example = "욕설 및 비방 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;
}
