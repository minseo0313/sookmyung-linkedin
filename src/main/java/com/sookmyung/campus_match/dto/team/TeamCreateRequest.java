package com.sookmyung.campus_match.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * 팀 생성 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "팀 생성 요청")
public class TeamCreateRequest {

    @NotBlank(message = "팀명은 필수입니다")
    @Schema(description = "팀명", example = "프로젝트 A팀", requiredMode = Schema.RequiredMode.REQUIRED)
    private String teamName;

    @Schema(description = "팀 설명", example = "웹 개발 프로젝트를 진행하는 팀입니다.")
    private String description;

    @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다")
    @Schema(description = "최대 인원", example = "5")
    private Integer maxMembers;

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;
}
