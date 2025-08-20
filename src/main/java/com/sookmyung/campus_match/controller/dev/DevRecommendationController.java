package com.sookmyung.campus_match.controller.dev;

import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.config.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Dev 환경용 추천 스텁 컨트롤러
 * 실제 추천 로직이 구현되기 전까지 dev 환경에서만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Profile("dev")
public class DevRecommendationController {

    private final CurrentUserResolver currentUserResolver;

    /**
     * Dev 환경용 사용자 추천 스텁
     */
    @GetMapping("/users")
    public ResponseEntity<ApiEnvelope<List<UserRecommendationResponse>>> getRecommendedUsers() {
        log.info("Dev 환경 - 사용자 추천 스텁 호출");
        
        List<UserRecommendationResponse> recommendations = Arrays.asList(
                UserRecommendationResponse.builder()
                        .userId(2L)
                        .score(0.95)
                        .build(),
                UserRecommendationResponse.builder()
                        .userId(3L)
                        .score(0.87)
                        .build(),
                UserRecommendationResponse.builder()
                        .userId(4L)
                        .score(0.82)
                        .build()
        );
        
        return ResponseEntity.ok(ApiEnvelope.success(recommendations));
    }

    /**
     * Dev 환경용 게시글 추천 스텁
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiEnvelope<List<PostRecommendationResponse>>> getRecommendedPosts() {
        log.info("Dev 환경 - 게시글 추천 스텁 호출");
        
        List<PostRecommendationResponse> recommendations = Arrays.asList(
                PostRecommendationResponse.builder()
                        .postId(1L)
                        .score(0.92)
                        .build(),
                PostRecommendationResponse.builder()
                        .postId(2L)
                        .score(0.88)
                        .build(),
                PostRecommendationResponse.builder()
                        .postId(3L)
                        .score(0.85)
                        .build()
        );
        
        return ResponseEntity.ok(ApiEnvelope.success(recommendations));
    }

    // Response DTOs
    @lombok.Builder
    public static class UserRecommendationResponse {
        public Long userId;
        public Double score;
    }

    @lombok.Builder
    public static class PostRecommendationResponse {
        public Long postId;
        public Double score;
    }
}

