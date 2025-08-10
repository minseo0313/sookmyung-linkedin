package com.sookmyung.campus_match.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 게시글 댓글 작성 요청 DTO.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostCommentCreateRequest {

    @Schema(description = "댓글 내용", example = "저도 참여하고 싶습니다!")
    @NotBlank
    @Size(max = 2000)
    private String content;
}
