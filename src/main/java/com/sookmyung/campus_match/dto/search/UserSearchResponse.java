package com.sookmyung.campus_match.dto.search;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 검색 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserSearchResponse {

    private Long id;
    private String studentId;
    private String department;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public static UserSearchResponse from(com.sookmyung.campus_match.domain.user.User user) {
        return UserSearchResponse.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .department(user.getDepartment())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
