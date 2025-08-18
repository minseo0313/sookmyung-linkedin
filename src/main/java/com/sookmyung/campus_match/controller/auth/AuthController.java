package com.sookmyung.campus_match.controller.auth;

import com.sookmyung.campus_match.dto.auth.*;
import com.sookmyung.campus_match.dto.user.UserResponse;
import com.sookmyung.campus_match.dto.common.ApiEnvelope;
import com.sookmyung.campus_match.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 컨트롤러
 */
@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "CREATED",
                              "message": "created",
                              "data": {
                                "id": 1,
                                "studentId": "20240001",
                                "department": "컴퓨터학부",
                                "name": "홍길동",
                                "birthDate": "2000-01-01",
                                "phoneNumber": "010-1234-5678",
                                "email": "student@sookmyung.ac.kr",
                                "approvalStatus": "PENDING",
                                "isDeleted": false,
                                "createdAt": "2025-01-27T10:30:00",
                                "updatedAt": "2025-01-27T10:30:00"
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "409", description = "중복된 사용자")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiEnvelope<UserResponse>> register(
            @Valid @RequestBody UserRegisterRequest request) {
        
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + user.getId())
                .body(ApiEnvelope.created(user));
    }

    @Operation(summary = "로그인", description = "학번과 비밀번호로 로그인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "success",
                              "data": {
                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                "tokenType": "Bearer",
                                "expiresIn": 3600
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<TokenResponse>> login(
            @Valid @RequestBody UserLoginRequest request) {
        
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(ApiEnvelope.success(token));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiEnvelope<Void>> logout() {
        // TODO: 현재 사용자 정보를 가져와서 전달
        authService.logout("current-user");
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiEnvelope.okMessage());
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ApiEnvelope<Void>> deleteUser() {
        // TODO: 현재 사용자 정보를 가져와서 전달
        authService.deleteAccount("current-user");
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiEnvelope.okMessage());
    }

    @Operation(summary = "사용자 뱃지 조회", description = "사용자의 뱃지 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뱃지 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "success",
                              "data": {
                                "userId": 1,
                                "badgeType": "APPROVED",
                                "badgeName": "승인된 사용자",
                                "description": "관리자 승인을 받은 사용자",
                                "isActive": true
                              },
                              "timestamp": "2025-01-27T10:30:00Z"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}/badge")
    public ResponseEntity<ApiEnvelope<BadgeResponse>> getUserBadge(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long id) {
        
        BadgeResponse badge = authService.checkBadge(id);
        return ResponseEntity.ok(ApiEnvelope.success(badge));
    }

    @Operation(summary = "비밀번호 확인", description = "현재 비밀번호를 확인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 확인 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치")
    })
    @PostMapping("/verify-password")
    public ResponseEntity<ApiEnvelope<Void>> verifyPassword(
            @Valid @RequestBody VerifyPasswordRequest request) {
        
        // TODO: 현재 사용자 정보를 가져와서 전달
        boolean isValid = authService.verifyPassword(request, "current-user");
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiEnvelope.error("INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."));
        }
        return ResponseEntity.ok(ApiEnvelope.okMessage());
    }
}
