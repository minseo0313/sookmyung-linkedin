package com.sookmyung.campus_match.dto.user;

import lombok.*;

/**
 * 관심사 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InterestResponse {

    private Long id;
    private String name;
    private String description;
}
