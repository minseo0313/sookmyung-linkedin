package com.sookmyung.campus_match.domain.common.enums;

public enum MessageReportReason {
    SPAM("스팸"),
    HARASSMENT("괴롭힘"),
    INAPPROPRIATE_CONTENT("부적절한 내용"),
    COMMERCIAL("상업적 목적"),
    OTHER("기타");

    private final String description;

    MessageReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
