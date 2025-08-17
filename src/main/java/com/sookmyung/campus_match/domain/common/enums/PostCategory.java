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
}
