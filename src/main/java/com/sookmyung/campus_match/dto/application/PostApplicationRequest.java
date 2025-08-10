package com.sookmyung.campus_match.dto.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 게시글 신청(참여 요청) DTO.
 * - 신청 시 선택적으로 메시지를 함께 보낼 수 있음
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostApplicationRequest {

    @Schema(description = "신청 메시지", example = "백엔드 개발자로 참여하고 싶습니다. Spring Boot 경험이 있습니다.")
    @Size(max = 2000)
    private String message;
}
