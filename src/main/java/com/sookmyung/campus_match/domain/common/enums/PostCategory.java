package com.sookmyung.campus_match.domain.common.enums;

public enum PostCategory {
    PROJECT("프로젝트"),
    STUDY("스터디"),
    COMPETITION("공모전"),
    HACKATHON("해커톤"),
    INTERNSHIP("인턴십"),
    VOLUNTEER("봉사활동"),
    OTHER("기타");

    private final String description;

    PostCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 문자열로부터 PostCategory를 안전하게 가져옵니다.
     * WHY: dev 환경에서 카테고리 파싱 실패 시 명확한 에러 메시지 제공
     */
    public static PostCategory from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리가 비어있습니다");
        }
        
        String upperValue = value.trim().toUpperCase();
        for (PostCategory category : values()) {
            if (category.name().equals(upperValue)) {
                return category;
            }
        }
        
        // 지원되는 카테고리 목록 생성
        String supportedCategories = String.join(", ", 
                java.util.Arrays.stream(values())
                        .map(PostCategory::name)
                        .toArray(String[]::new));
        
        throw new IllegalArgumentException(
                String.format("지원하지 않는 카테고리입니다: '%s'. 지원되는 카테고리: %s", 
                        value, supportedCategories));
    }
}
