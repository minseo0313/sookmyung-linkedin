package com.sookmyung.campus_match.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 기존 스레드에서 메시지 답장 요청 DTO.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageReplyRequest {

    @Schema(description = "메시지 스레드 ID", example = "15")
    @NotNull
    private Long threadId;

    @Schema(description = "메시지 내용", example = "네, 일정 공유드리겠습니다.")
    @NotBlank
    @Size(max = 4000)
    private String content;
}
