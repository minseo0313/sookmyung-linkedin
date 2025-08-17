package com.sookmyung.campus_match.dto.message;

import com.sookmyung.campus_match.domain.common.enums.MessageReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "메시지 신고 요청")
public class MessageReportRequest {

    @NotNull(message = "신고 사유는 필수입니다")
    @Schema(description = "신고 사유", example = "SPAM")
    private MessageReportReason reason;

    @Size(max = 500, message = "신고 상세 설명은 500자 이하여야 합니다")
    @Schema(description = "신고 상세 설명", example = "스팸 메시지입니다.")
    private String description;
}
