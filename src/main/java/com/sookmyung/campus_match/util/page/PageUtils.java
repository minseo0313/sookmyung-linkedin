package com.sookmyung.campus_match.util.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 페이징/정렬 파라미터 파싱 유틸리티
 */
@Component
public class PageUtils {

    private final Environment environment;

    public PageUtils(Environment environment) {
        this.environment = environment;
    }

    // 허용된 정렬 필드 화이트리스트 (공통)
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "likeCount", "viewCount", "commentCount",
            "title", "postTitle", "name", "department", "studentId", "email"
    );

    // PostApplication 전용 정렬 필드 (API 키 -> JPA 속성 경로 매핑)
    private static final Map<String, String> POST_APPLICATION_SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "status", "status", 
            "applicantId", "applicant.id"
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
     * PostApplication 전용 페이징 파라미터를 Pageable로 변환
     */
    public Pageable createPostApplicationPageable(Integer page, Integer size, String sort) {
        int pageNumber = page != null ? Math.max(0, page) : DEFAULT_PAGE;
        int pageSize = size != null ? Math.min(Math.max(1, size), MAX_SIZE) : DEFAULT_SIZE;
        
        Sort sortObj = parsePostApplicationSort(sort != null ? sort : DEFAULT_SORT);
        
        return PageRequest.of(pageNumber, pageSize, sortObj);
    }

    /**
     * 정렬 문자열을 Sort 객체로 파싱 (공통)
     */
    public Sort parseSort(String sortString) {
        if (sortString == null || sortString.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort format. Expected: field,direction");
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        // 허용된 필드인지 검증
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Sort field '" + field + "' is not allowed. Allowed fields: " + ALLOWED_SORT_FIELDS);
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort direction. Expected: asc or desc");
        }

        return Sort.by(sortDirection, field);
    }

    /**
     * PostApplication 전용 정렬 문자열을 Sort 객체로 파싱
     */
    public Sort parsePostApplicationSort(String sortString) {
        if (sortString == null || sortString.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort format. Expected: field,direction");
        }

        String apiKey = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        // PostApplication 허용된 필드인지 검증
        if (!POST_APPLICATION_SORT_FIELDS.containsKey(apiKey)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Sort field '" + apiKey + "' is not allowed for PostApplication. Allowed fields: " + POST_APPLICATION_SORT_FIELDS.keySet());
        }

        // API 키를 JPA 속성 경로로 매핑
        String jpaField = POST_APPLICATION_SORT_FIELDS.get(apiKey);

        Sort.Direction sortDirection;
        switch (direction) {
            case "asc":
                sortDirection = Sort.Direction.ASC;
                break;
            case "desc":
                sortDirection = Sort.Direction.DESC;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort direction. Expected: asc or desc");
        }

        return Sort.by(sortDirection, jpaField);
    }

    /**
     * 검색 키워드 검증
     */
    public void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword cannot be empty");
        }
        
        String trimmedKeyword = keyword.trim();
        
        // WHY: dev 환경에서 키워드 검증 완화 (1글자도 허용)
        boolean isDevProfile = environment.acceptsProfiles("dev");
        if (isDevProfile) {
            if (trimmedKeyword.length() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword cannot be empty");
            }
        } else {
            if (trimmedKeyword.length() < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword must be at least 2 characters long");
            }
        }
        
        // 공백이나 특수문자만으로 구성된 경우 검증 (한글 포함)
        if (trimmedKeyword.matches("^[\\s\\p{Punct}]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword cannot contain only whitespace or special characters");
        }
    }

    /**
     * 허용된 정렬 필드 목록 반환 (공통)
     */
    public Set<String> getAllowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }

    /**
     * PostApplication 허용된 정렬 필드 목록 반환
     */
    public Set<String> getPostApplicationSortFields() {
        return POST_APPLICATION_SORT_FIELDS.keySet();
    }
}
