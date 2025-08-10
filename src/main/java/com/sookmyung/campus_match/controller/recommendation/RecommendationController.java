package com.sookmyung.campus_match.controller.recommendation;

import com.sookmyung.campus_match.domain.recommendation.UserRecommendation;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.service.recommendation.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI 추천", description = "AI 기반 사용자 추천 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "사용자 추천 목록 조회", description = "현재 로그인한 사용자에게 추천되는 다른 사용자 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRecommendation>>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // TODO: UserDetails에서 userId 추출 로직 구현 필요
        // 임시로 하드코딩된 userId 사용
        Long userId = 1L; // 실제로는 userDetails에서 추출
        
        List<UserRecommendation> recommendations = recommendationService.getRecommendationsForUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    @Operation(summary = "사용자 추천 재생성", description = "현재 로그인한 사용자의 추천 목록을 재생성합니다.")
    @PostMapping("/regenerate")
    public ResponseEntity<ApiResponse<List<UserRecommendation>>> regenerateRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // TODO: UserDetails에서 userId 추출 로직 구현 필요
        Long userId = 1L; // 실제로는 userDetails에서 추출
        
        List<UserRecommendation> recommendations = recommendationService.generateRecommendationsForUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    @Operation(summary = "전체 사용자 추천 재생성 (관리자용)", description = "모든 사용자의 추천 목록을 재생성합니다.")
    @PostMapping("/regenerate-all")
    public ResponseEntity<ApiResponse<String>> regenerateAllRecommendations() {
        recommendationService.regenerateAllRecommendations();
        
        return ResponseEntity.ok(ApiResponse.success("모든 사용자의 추천 목록이 재생성되었습니다."));
    }
}
