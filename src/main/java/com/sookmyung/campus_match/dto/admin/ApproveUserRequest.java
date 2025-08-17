package com.sookmyung.campus_match.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 회원 승인 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "회원 승인/반려 요청")
public class ApproveUserRequest {

    @NotNull(message = "승인 여부는 필수입니다")
    @Schema(description = "승인 여부", example = "true", required = true)
    private Boolean approved;
}
