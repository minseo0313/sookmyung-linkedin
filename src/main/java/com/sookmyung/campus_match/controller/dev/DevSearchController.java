package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.search.PostSearchResponse;
import com.sookmyung.campus_match.dto.search.UserSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dev 환경용 검색 스텁 컨트롤러
 * WHY: dev 환경에서 한글 키워드 URL 인코딩 문제를 해결하고, 빈/짧은 키워드도 허용
 * 
 * 주의: 한글/특수문자 키워드는 반드시 URL 인코딩이 필요합니다.
 * - 올바른 예: /api/search/posts?keyword=%EA%B0%9C%EB%B0%9C
 * - 잘못된 예: /api/search/posts?keyword=개발 (400 에러 발생)
 * 
 * 테스트 편의를 위한 POST 대체 엔드포인트:
 * - POST /api/search/posts/_dev
 * - POST /api/search/users/_dev
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@Profile("dev")
public class DevSearchController {

    @GetMapping("/posts")
    public ResponseEntity<ApiEnvelope<PageResponse<PostSearchResponse>>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        log.info("Dev 환경 - 게시글 검색 스텁 호출: keyword={}, page={}, size={}", keyword, page, size);
        
        // WHY: dev 환경에서는 키워드가 빈/짧아도 200 + 빈 결과 반환
        PageResponse<PostSearchResponse> emptyResponse = PageResponse.<PostSearchResponse>builder()
                .page(page != null ? page : 0)
                .size(size != null ? size : 20)
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .content(List.of())
                .build();
        
        return ResponseEntity.ok(ApiEnvelope.success(emptyResponse));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiEnvelope<PageResponse<UserSearchResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        log.info("Dev 환경 - 사용자 검색 스텁 호출: keyword={}, page={}, size={}", keyword, page, size);
        
        // WHY: dev 환경에서는 키워드가 빈/짧아도 200 + 빈 결과 반환
        PageResponse<UserSearchResponse> emptyResponse = PageResponse.<UserSearchResponse>builder()
                .page(page != null ? page : 0)
                .size(size != null ? size : 20)
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .content(List.of())
                .build();
        
        return ResponseEntity.ok(ApiEnvelope.success(emptyResponse));
    }

    // WHY: dev 환경에서 POST 바디로 검색하여 URL 인코딩 문제 회피
    @PostMapping("/posts/_dev")
    public ResponseEntity<ApiEnvelope<PageResponse<PostSearchResponse>>> searchPostsPost(
            @RequestBody(required = false) SearchRequest request) {
        
        String keyword = request != null ? request.keyword : null;
        Integer page = request != null ? request.page : 0;
        Integer size = request != null ? request.size : 20;
        
        log.info("Dev 환경 - 게시글 검색 POST 스텁 호출: keyword={}, page={}, size={}", keyword, page, size);
        
        PageResponse<PostSearchResponse> emptyResponse = PageResponse.<PostSearchResponse>builder()
                .page(page != null ? page : 0)
                .size(size != null ? size : 20)
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .content(List.of())
                .build();
        
        return ResponseEntity.ok(ApiEnvelope.success(emptyResponse));
    }

    @PostMapping("/users/_dev")
    public ResponseEntity<ApiEnvelope<PageResponse<UserSearchResponse>>> searchUsersPost(
            @RequestBody(required = false) SearchRequest request) {
        
        String keyword = request != null ? request.keyword : null;
        Integer page = request != null ? request.page : 0;
        Integer size = request != null ? request.size : 20;
        
        log.info("Dev 환경 - 사용자 검색 POST 스텁 호출: keyword={}, page={}, size={}", keyword, page, size);
        
        PageResponse<UserSearchResponse> emptyResponse = PageResponse.<UserSearchResponse>builder()
                .page(page != null ? page : 0)
                .size(size != null ? size : 20)
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .hasPrevious(false)
                .content(List.of())
                .build();
        
        return ResponseEntity.ok(ApiEnvelope.success(emptyResponse));
    }

    // 검색 요청 DTO
    public static class SearchRequest {
        public String keyword;
        public Integer page;
        public Integer size;
    }
}
