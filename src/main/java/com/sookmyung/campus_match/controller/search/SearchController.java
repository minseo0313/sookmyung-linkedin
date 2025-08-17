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

@Tag(name = "검색", description = "게시글 및 사용자 검색 관련 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "게시글 검색", description = "키워드와 카테고리로 게시글을 검색합니다.")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostSearchResponse>>> searchPosts(
            @Parameter(description = "검색 키워드", example = "웹 개발")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "게시글 카테고리", example = "개발")
            @RequestParam(required = false) String category,
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=createdAt,desc")
            Pageable pageable) {
        
        Page<PostSearchResponse> posts = searchService.searchPosts(keyword, category, pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @Operation(summary = "사용자 검색", description = "키워드와 학과로 사용자를 검색합니다.")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSearchResponse>>> searchUsers(
            @Parameter(description = "검색 키워드", example = "김철수")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "학과", example = "컴퓨터공학과")
            @RequestParam(required = false) String department,
            @Parameter(description = "관심사", example = "프로그래밍")
            @RequestParam(required = false) String interest,
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=name,asc")
            Pageable pageable) {
        
        Page<UserSearchResponse> users = searchService.searchUsers(keyword, department, interest, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "인기 게시글", description = "조회수와 좋아요가 높은 인기 게시글을 조회합니다.")
    @GetMapping("/posts/popular")
    public ResponseEntity<ApiResponse<List<PostSearchResponse>>> getPopularPosts(
            @Parameter(description = "조회할 게시글 수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<PostSearchResponse> posts = searchService.getPopularPosts(limit);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @Operation(summary = "카테고리별 최신 게시글", description = "특정 카테고리의 최신 게시글을 조회합니다.")
    @GetMapping("/posts/category/{category}")
    public ResponseEntity<ApiResponse<List<PostSearchResponse>>> getLatestPostsByCategory(
            @Parameter(description = "게시글 카테고리", example = "개발")
            @PathVariable String category,
            @Parameter(description = "조회할 게시글 수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<PostSearchResponse> posts = searchService.getLatestPostsByCategory(category, limit);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @Operation(summary = "맞춤 게시글 추천", description = "사용자에게 맞춤 게시글을 추천합니다.")
    @GetMapping("/posts/recommended")
    public ResponseEntity<ApiResponse<List<PostSearchResponse>>> getRecommendedPosts(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam Long userId,
            @Parameter(description = "추천할 게시글 수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<PostSearchResponse> posts = searchService.getRecommendedPostsForUser(userId, limit);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @Operation(summary = "검색 통계", description = "전체 게시글과 사용자 수 등의 검색 통계를 조회합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<SearchService.SearchStatistics>> getSearchStatistics() {
        SearchService.SearchStatistics statistics = searchService.getSearchStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @Operation(summary = "통합 검색", description = "게시글과 사용자를 동시에 검색합니다.")
    @GetMapping("/unified")
    public ResponseEntity<ApiResponse<UnifiedSearchResponse>> unifiedSearch(
            @Parameter(description = "검색 키워드", example = "웹 개발")
            @RequestParam String keyword,
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=createdAt,desc")
            Pageable pageable) {
        
        // 게시글과 사용자 동시 검색
        Page<PostSearchResponse> posts = searchService.searchPosts(keyword, null, pageable);
        Page<UserSearchResponse> users = searchService.searchUsers(keyword, null, null, pageable);
        
        UnifiedSearchResponse response = UnifiedSearchResponse.builder()
                .posts(posts)
                .users(users)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 통합 검색 응답 DTO
     */
    public static class UnifiedSearchResponse {
        private final Page<PostSearchResponse> posts;
        private final Page<UserSearchResponse> users;

        public UnifiedSearchResponse(Page<PostSearchResponse> posts, Page<UserSearchResponse> users) {
            this.posts = posts;
            this.users = users;
        }

        public Page<PostSearchResponse> getPosts() { return posts; }
        public Page<UserSearchResponse> getUsers() { return users; }

        public static UnifiedSearchResponseBuilder builder() {
            return new UnifiedSearchResponseBuilder();
        }

        public static class UnifiedSearchResponseBuilder {
            private Page<PostSearchResponse> posts;
            private Page<UserSearchResponse> users;

            public UnifiedSearchResponseBuilder posts(Page<PostSearchResponse> posts) {
                this.posts = posts;
                return this;
            }

            public UnifiedSearchResponseBuilder users(Page<UserSearchResponse> users) {
                this.users = users;
                return this;
            }

            public UnifiedSearchResponse build() {
                return new UnifiedSearchResponse(posts, users);
            }
        }
    }
}
