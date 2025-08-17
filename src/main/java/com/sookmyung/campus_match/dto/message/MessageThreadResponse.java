package com.sookmyung.campus_match.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sookmyung.campus_match.domain.message.MessageThread;
import com.sookmyung.campus_match.domain.message.Message;
import com.sookmyung.campus_match.domain.common.enums.StartedFromType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 메시지 스레드 응답 DTO.
 * - 마지막 메시지 미리보기, 미읽기 개수 포함
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageThreadResponse {

    @Schema(description = "스레드 ID", example = "15")
    private Long id;

    @Schema(description = "참여자 A ID", example = "5")
    private Long participantAId;

    @Schema(description = "참여자 B ID", example = "8")
    private Long participantBId;

    @Schema(description = "메시지 시작 경로 타입", example = "PROFILE")
    private StartedFromType startedFromType;

    @Schema(description = "메시지 시작 경로 ID", example = "3")
    private Long startedFromId;

    @Schema(description = "마지막 메시지 시각", example = "2025-08-10T15:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageAt;

    @Schema(description = "마지막 메시지 미리보기", example = "안녕하세요! 함께 프로젝트 하고 싶어요.")
    private String lastMessagePreview;

    @Schema(description = "참여자 A의 미읽기 개수", example = "0")
    private int unreadCountA;

    @Schema(description = "참여자 B의 미읽기 개수", example = "2")
    private int unreadCountB;

    public static MessageThreadResponse from(MessageThread thread) {
        return MessageThreadResponse.builder()
                .id(thread.getId())
                .participantAId(thread.getParticipantAId())
                .participantBId(thread.getParticipantBId())
                .startedFromType(thread.getStartedFromType())
                .startedFromId(thread.getStartedFromId())
                .lastMessageAt(thread.getLastMessageAt())
                .lastMessagePreview(thread.getLastMessagePreview())
                .unreadCountA(thread.getUnreadCountA())
                .unreadCountB(thread.getUnreadCountB())
                .build();
    }

    public static MessageThreadResponse from(MessageThread thread, Message lastMessage) {
        return MessageThreadResponse.builder()
                .id(thread.getId())
                .participantAId(thread.getParticipantAId())
                .participantBId(thread.getParticipantBId())
                .startedFromType(thread.getStartedFromType())
                .startedFromId(thread.getStartedFromId())
                .lastMessageAt(thread.getLastMessageAt())
                .lastMessagePreview(lastMessage != null ? lastMessage.getContent() : thread.getLastMessagePreview())
                .unreadCountA(thread.getUnreadCountA())
                .unreadCountB(thread.getUnreadCountB())
                .build();
    }
}
