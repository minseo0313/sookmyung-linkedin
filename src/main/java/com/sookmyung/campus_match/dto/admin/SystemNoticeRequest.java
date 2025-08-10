package com.sookmyung.campus_match.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 시스템 공지 등록/수정 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SystemNoticeRequest {

    @Schema(description = "공지 제목", example = "시스템 점검 안내")
    private String title;

    @Schema(description = "공지 내용", example = "8월 15일 00:00~06:00 서버 점검이 예정되어 있습니다.")
    private String content;

    @Schema(description = "공지 활성화 여부", example = "true")
    private boolean active;
}
