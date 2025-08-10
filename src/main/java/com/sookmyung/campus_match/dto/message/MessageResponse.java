package com.sookmyung.campus_match.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sookmyung.campus_match.domain.message.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 메시지 단건 응답 DTO.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageResponse {

    @Schema(description = "메시지 ID", example = "100")
    private Long id;

    @Schema(description = "스레드 ID", example = "15")
    private Long threadId;

    @Schema(description = "발신자 ID", example = "5")
    private Long senderId;

    @Schema(description = "내용", example = "안녕하세요! 함께 프로젝트 하고 싶어요.")
    private String content;

    @Schema(description = "삭제 여부", example = "false")
    private boolean deleted;

    @Schema(description = "작성일시", example = "2025-08-10T14:55:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .threadId(message.getThreadId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .deleted(message.isDeleted())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
