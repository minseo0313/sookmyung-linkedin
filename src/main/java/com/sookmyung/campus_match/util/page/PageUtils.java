package com.sookmyung.campus_match.util.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 페이징/정렬 파라미터 파싱 유틸리티
 */
@Component
public class PageUtils {

    // 허용된 정렬 필드 화이트리스트
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "likeCount", "viewCount", "commentCount",
            "title", "postTitle", "name", "department", "studentId", "email"
    );

    // 기본 페이징 설정
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT = "createdAt,desc";

    /**
     * 페이징 파라미터를 Pageable로 변환
     */
    public Pageable createPageable(Integer page, Integer size, String sort) {
        int pageNumber = page != null ? Math.max(0, page) : DEFAULT_PAGE;
        int pageSize = size != null ? Math.min(Math.max(1, size), MAX_SIZE) : DEFAULT_SIZE;
        
        Sort sortObj = parseSort(sort != null ? sort : DEFAULT_SORT);
        
        return PageRequest.of(pageNumber, pageSize, sortObj);
    }

    /**
     * 정렬 문자열을 Sort 객체로 파싱
     */
    public Sort parseSort(String sortString) {
        if (sortString == null || sortString.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort format. Expected: field,direction");
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        // 허용된 필드인지 검증
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new IllegalArgumentException("Sort field '" + field + "' is not allowed. Allowed fields: " + ALLOWED_SORT_FIELDS);
        }

        Sort.Direction sortDirection;
        switch (direction) {
            case "asc":
                sortDirection = Sort.Direction.ASC;
                break;
            case "desc":
                sortDirection = Sort.Direction.DESC;
                break;
            default:
                throw new IllegalArgumentException("Invalid sort direction. Expected: asc or desc");
        }

        return Sort.by(sortDirection, field);
    }

    /**
     * 검색 키워드 검증
     */
    public void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.length() < 2) {
            throw new IllegalArgumentException("Search keyword must be at least 2 characters long");
        }
        
        // 공백이나 특수문자만으로 구성된 경우 검증
        if (trimmedKeyword.matches("^[\\s\\W]+$")) {
            throw new IllegalArgumentException("Search keyword cannot contain only whitespace or special characters");
        }
    }

    /**
     * 허용된 정렬 필드 목록 반환
     */
    public Set<String> getAllowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }
}
