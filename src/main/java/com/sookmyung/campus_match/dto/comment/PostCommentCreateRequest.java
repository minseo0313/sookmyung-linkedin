package com.sookmyung.campus_match.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "댓글 작성 요청")
public class PostCommentCreateRequest {

    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 1, max = 500, message = "댓글 내용은 1자 이상 500자 이하여야 합니다")
    @Schema(description = "댓글 내용", example = "관심있습니다! 연락드릴게요.")
    private String content;
}
