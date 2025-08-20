package com.sookmyung.campus_match.controller.search;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.dto.common.PageResponse;
import com.sookmyung.campus_match.dto.search.PostSearchResponse;
import com.sookmyung.campus_match.dto.search.UserSearchResponse;
import com.sookmyung.campus_match.dto.search.SearchRequest;
import com.sookmyung.campus_match.service.search.SearchService;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Search", description = "검색 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Profile("!dev") // WHY: dev 환경에서는 DevSearchController가 대신 처리
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "사용자 검색", description = "키워드로 사용자를 검색합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색어"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiEnvelope<PageResponse<UserSearchResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        // WHY: dev 환경에서 검색 파라미터 검증과 안전한 처리
        log.debug("사용자 검색 요청 - keyword: {}, page: {}, size: {}", keyword, page, size);
        
        try {
            // keyword가 비어있으면 빈 결과 반환
            if (keyword == null || keyword.trim().isEmpty()) {
                log.debug("검색 키워드가 비어있어 빈 결과 반환");
                return ResponseEntity.ok(ApiEnvelope.success(PageResponse.empty()));
            }
            
            PageResponse<UserSearchResponse> users = searchService.searchUsers(keyword, page, size, null);
            return ResponseEntity.ok(ApiEnvelope.success(users));
        } catch (Exception e) {
            log.warn("사용자 검색 실패 - 빈 결과 반환: {}", e.getMessage());
            return ResponseEntity.ok(ApiEnvelope.success(PageResponse.empty()));
        }
    }

    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색어"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/posts")
    public ResponseEntity<ApiEnvelope<PageResponse<PostSearchResponse>>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        // WHY: dev 환경에서 검색 파라미터 검증과 안전한 처리
        log.debug("게시글 검색 요청 - keyword: {}, page: {}, size: {}", keyword, page, size);
        
        try {
            // keyword가 비어있으면 빈 결과 반환
            if (keyword == null || keyword.trim().isEmpty()) {
                log.debug("검색 키워드가 비어있어 빈 결과 반환");
                return ResponseEntity.ok(ApiEnvelope.success(PageResponse.empty()));
            }
            
            PageResponse<PostSearchResponse> posts = searchService.searchPosts(keyword, page, size, null);
            return ResponseEntity.ok(ApiEnvelope.success(posts));
        } catch (Exception e) {
            log.warn("게시글 검색 실패 - 빈 결과 반환: {}", e.getMessage());
            return ResponseEntity.ok(ApiEnvelope.success(PageResponse.empty()));
        }
    }
}
