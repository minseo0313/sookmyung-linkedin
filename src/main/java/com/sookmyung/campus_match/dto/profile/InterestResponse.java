package com.sookmyung.campus_match.dto.profile;

import lombok.*;

/**
 * 관심사 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterestResponse {
    
    private Long id;
    private String name;
    private String description;
    private String category;
}
