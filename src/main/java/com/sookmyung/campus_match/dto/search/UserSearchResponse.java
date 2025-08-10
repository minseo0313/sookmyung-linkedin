package com.sookmyung.campus_match.dto.search;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.user.enum_.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 검색 결과 응답 DTO.
 * - 비공개 정보(생년월일, 전화번호, 학번, 이메일)는 제외
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserSearchResponse {

    @Schema(description = "사용자 ID", example = "5")
    private Long id;

    @Schema(description = "로그인 아이디(=학번)", example = "20251234")
    private String username;

    @Schema(description = "이름", example = "남민서")
    private String fullName;

    @Schema(description = "학과", example = "소프트웨어융합전공")
    private String department;

    @Schema(description = "승인 상태", example = "APPROVED")
    private ApprovalStatus approvalStatus;

    public static UserSearchResponse from(User user) {
        return UserSearchResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .department(user.getDepartment())
                .approvalStatus(user.getApprovalStatus())
                .build();
    }
}
