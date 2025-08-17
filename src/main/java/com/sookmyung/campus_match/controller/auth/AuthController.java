package com.sookmyung.campus_match.controller.auth;

import com.sookmyung.campus_match.dto.auth.BadgeResponse;
import com.sookmyung.campus_match.dto.auth.TokenResponse;
import com.sookmyung.campus_match.dto.auth.UserLoginRequest;
import com.sookmyung.campus_match.dto.auth.UserRegisterRequest;
import com.sookmyung.campus_match.dto.auth.UserResponse;
import com.sookmyung.campus_match.dto.auth.VerifyPasswordRequest;
import com.sookmyung.campus_match.dto.common.ApiResponse;
import com.sookmyung.campus_match.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "인증", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "회원 가입", 
        description = "새로운 사용자를 등록합니다. 권한성 필드(role, approvalStatus, operator)는 서버에서 기본값으로 설정됩니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", 
                description = "회원가입 성공 - 사용자가 생성되었습니다",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", 
                description = "입력값 검증 실패 - 필드별 오류 정보가 포함됩니다",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", 
                description = "중복 데이터 - 이미 존재하는 이메일 또는 학번",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", 
                description = "서버 내부 오류 - 시스템 오류가 발생했습니다",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
                )
            )
        }
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRegisterRequest request) {
        
        UserResponse user = authService.register(request);
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(user.getId())
                .toUri()
        ).body(ApiResponse.success(user));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody UserLoginRequest request) {
        
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        authService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("계정이 삭제되었습니다."));
    }

    @Operation(summary = "신원 승인 여부 확인", description = "사용자의 승인 상태와 배지 정보를 확인합니다.")
    @GetMapping("/{id}/badge")
    public ResponseEntity<ApiResponse<BadgeResponse>> checkBadge(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id) {
        
        BadgeResponse badge = authService.checkBadge(id);
        return ResponseEntity.ok(ApiResponse.success(badge));
    }

    @Operation(summary = "비밀번호 확인", description = "비밀번호 변경 전 현재 비밀번호를 확인합니다.")
    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<Boolean>> verifyPassword(
            @Valid @RequestBody VerifyPasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        boolean isValid = authService.verifyPassword(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }
}
