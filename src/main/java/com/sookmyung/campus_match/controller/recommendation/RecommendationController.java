package com.sookmyung.campus_match.controller.recommendation;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.dto.user.UserResponse;
import com.sookmyung.campus_match.service.recommendation.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI 추천", description = "AI 기반 사용자 추천 관련 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "AI 사용자 추천", description = "관심사/자기소개 기반으로 추천 사용자 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getRecommendedUsers(
            @Parameter(description = "페이징 정보", example = "page=0&size=10&sort=score,desc")
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Page<User> users = recommendationService.getRecommendedUsers(userDetails.getUsername(), pageable);
        Page<UserResponse> response = users.map(UserResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "맞춤 추천 사용자", description = "특정 사용자에 대한 맞춤 추천 사용자 목록을 조회합니다.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getRecommendedUsersForUser(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "추천할 사용자 수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<User> users = recommendationService.getRecommendedUsersForUser(userId, limit);
        List<UserResponse> response = users.stream().map(UserResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "관심사 기반 추천", description = "특정 관심사 기반으로 사용자를 추천합니다.")
    @GetMapping("/interests/{interestType}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByInterest(
            @Parameter(description = "관심사 타입", example = "PROGRAMMING")
            @PathVariable String interestType,
            @Parameter(description = "추천할 사용자 수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<User> users = recommendationService.getUsersByInterest(interestType, limit);
        List<UserResponse> response = users.stream().map(UserResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "학과 기반 추천", description = "같은 학과 사용자를 추천합니다.")
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByDepartment(
            @Parameter(description = "학과명", example = "컴퓨터학부")
            @PathVariable String department,
            @Parameter(description = "추천할 사용자 수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<User> users = recommendationService.getUsersByDepartment(department, limit);
        List<UserResponse> response = users.stream().map(UserResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
