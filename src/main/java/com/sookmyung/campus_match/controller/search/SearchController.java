package com.sookmyung.campus_match.controller.search;

import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.search.PostSearchResponse;
import com.sookmyung.campus_match.dto.search.UserSearchResponse;
import com.sookmyung.campus_match.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "검색", description = "검색 관련 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "통합 검색 자동완성", description = "게시글과 사용자 검색을 통합한 실시간 자동완성을 제공합니다.")
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getUnifiedSuggestions(
            @Parameter(description = "검색 키워드", example = "개발")
            @RequestParam String keyword) {
        
        List<String> suggestions = searchService.getUnifiedSearchSuggestions(keyword);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }

    @Operation(summary = "게시글 검색 자동완성", description = "게시글 제목과 내용에서 키워드를 추출하여 자동완성을 제공합니다.")
    @GetMapping("/posts/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getPostSuggestions(
            @Parameter(description = "검색 키워드", example = "백엔드")
            @RequestParam String keyword) {
        
        List<String> suggestions = searchService.getPostSearchSuggestions(keyword);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }

    @Operation(summary = "사용자 검색 자동완성", description = "사용자 이름과 학과에서 키워드를 추출하여 자동완성을 제공합니다.")
    @GetMapping("/users/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getUserSuggestions(
            @Parameter(description = "검색 키워드", example = "소프트웨어")
            @RequestParam String keyword) {
        
        List<String> suggestions = searchService.getUserSearchSuggestions(keyword);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }

    @Operation(summary = "게시글 검색", description = "게시글을 검색합니다.")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostSearchResponse>>> searchPosts(
            @Parameter(description = "검색 키워드", example = "개발")
            @RequestParam String keyword,
            Pageable pageable) {
        
        Page<PostSearchResponse> posts = searchService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @Operation(summary = "사용자 검색", description = "사용자를 검색합니다. (승인된 사용자만)")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSearchResponse>>> searchUsers(
            @Parameter(description = "검색 키워드", example = "김철수")
            @RequestParam String keyword,
            Pageable pageable) {
        
        Page<UserSearchResponse> users = searchService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
