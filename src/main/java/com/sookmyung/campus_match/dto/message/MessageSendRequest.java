package com.sookmyung.campus_match.dto.message;

import com.sookmyung.campus_match.domain.message.enum_.StartedFromType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 첫 메시지 전송 요청 DTO.
 * - 프로필(인사하기) 또는 게시글(문의하기)에서 시작
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageSendRequest {

    @Schema(description = "수신자 ID", example = "8")
    @NotNull
    private Long recipientId;

    @Schema(description = "메시지 시작 경로 타입", example = "PROFILE")
    @NotNull
    private StartedFromType startedFromType;

    @Schema(description = "메시지 시작 경로 ID (PROFILE.id 또는 POST.id)", example = "3")
    private Long startedFromId;

    @Schema(description = "메시지 내용", example = "안녕하세요! 함께 프로젝트 하고 싶어요.")
    @NotBlank
    @Size(max = 4000)
    private String content;
}
