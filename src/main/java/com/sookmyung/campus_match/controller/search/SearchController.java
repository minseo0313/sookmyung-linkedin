package com.sookmyung.campus_match.controller.search;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.search.PostSearchResponse;
import com.sookmyung.campus_match.dto.search.UserSearchResponse;
import com.sookmyung.campus_match.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 검색 관련 컨트롤러
 */
@Tag(name = "Search", description = "검색 관련 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Validated
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "사용자 검색", description = "키워드로 사용자를 검색합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색어")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiEnvelope<PageResponse<UserSearchResponse>>> searchUsers(
            @Parameter(description = "검색 키워드 (최소 2자)", example = "홍길동")
            @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 (필드,방향)", example = "name,asc")
            @RequestParam(defaultValue = "name,asc") String sort) {
        
        PageResponse<UserSearchResponse> users = searchService.searchUsers(keyword, page, size, sort);
        return ResponseEntity.ok(ApiEnvelope.success(users));
    }

    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색어")
    })
    @GetMapping("/posts")
    public ResponseEntity<ApiEnvelope<PageResponse<PostSearchResponse>>> searchPosts(
            @Parameter(description = "검색 키워드 (최소 2자)", example = "프로젝트")
            @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 (필드,방향)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        PageResponse<PostSearchResponse> posts = searchService.searchPosts(keyword, page, size, sort);
        return ResponseEntity.ok(ApiEnvelope.success(posts));
    }
}
