package com.sookmyung.campus_match.dto.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sookmyung.campus_match.domain.post.PostApplication;
import com.sookmyung.campus_match.domain.post.enum_.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 신청(참여 요청) 응답 DTO.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostApplicationResponse {

    @Schema(description = "신청 ID", example = "100")
    private Long id;

    @Schema(description = "게시글 ID", example = "10")
    private Long postId;

    @Schema(description = "신청자 ID", example = "5")
    private Long applicantId;

    @Schema(description = "신청 메시지", example = "백엔드 개발자로 참여하고 싶습니다.")
    private String message;

    @Schema(description = "신청 상태", example = "PENDING")
    private ApplicationStatus status;

    @Schema(description = "신청일시", example = "2025-08-10T14:40:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static PostApplicationResponse from(PostApplication application) {
        return PostApplicationResponse.builder()
                .id(application.getId())
                .postId(application.getPostId())
                .applicantId(application.getApplicantId())
                .message(application.getMessage())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
