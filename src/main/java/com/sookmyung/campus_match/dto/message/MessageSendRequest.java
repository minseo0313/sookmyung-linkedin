package com.sookmyung.campus_match.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "메시지 전송 요청")
public class MessageSendRequest {

    @NotNull(message = "수신자 ID는 필수입니다")
    @Schema(description = "수신자 ID", example = "1")
    private Long receiverId;

    @NotBlank(message = "메시지 제목은 필수입니다")
    @Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하여야 합니다")
    @Schema(description = "메시지 제목", example = "프로젝트 협업 제안")
    private String title;

    @NotBlank(message = "메시지 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "내용은 1자 이상 1000자 이하여야 합니다")
    @Schema(description = "메시지 내용", example = "안녕하세요! 프로젝트에 함께 참여하실 분을 찾고 있습니다.")
    private String content;
}
