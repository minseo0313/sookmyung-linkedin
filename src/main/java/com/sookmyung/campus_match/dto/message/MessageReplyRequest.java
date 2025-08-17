package com.sookmyung.campus_match.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "메시지 답장 요청")
public class MessageReplyRequest {

    @NotBlank(message = "답장 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "답장 내용은 1자 이상 1000자 이하여야 합니다")
    @Schema(description = "답장 내용", example = "네, 좋습니다! 언제 만나서 이야기해볼까요?")
    private String content;
}
