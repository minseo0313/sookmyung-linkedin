package com.sookmyung.campus_match.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 회원 승인 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApproveUserRequest {

    @Schema(description = "승인할 회원 ID", example = "10")
    private Long userId;

    @Schema(description = "승인 여부", example = "true")
    private boolean approved;
}
