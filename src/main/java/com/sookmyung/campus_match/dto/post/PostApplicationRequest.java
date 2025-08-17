package com.sookmyung.campus_match.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "게시글 지원 요청")
public class PostApplicationRequest {

    @NotBlank(message = "지원 메시지는 필수입니다")
    @Size(min = 10, max = 1000, message = "지원 메시지는 10자 이상 1000자 이하여야 합니다")
    @Schema(description = "지원 메시지", example = "프론트엔드 개발 경험이 있어서 도움이 될 것 같습니다.")
    private String message;
}
