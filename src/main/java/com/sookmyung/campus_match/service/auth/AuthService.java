package com.sookmyung.campus_match.service.auth;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.dto.auth.BadgeResponse;
import com.sookmyung.campus_match.dto.auth.TokenResponse;
import com.sookmyung.campus_match.dto.auth.UserLoginRequest;
import com.sookmyung.campus_match.dto.auth.UserRegisterRequest;
import com.sookmyung.campus_match.dto.auth.UserResponse;
import com.sookmyung.campus_match.dto.auth.VerifyPasswordRequest;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        // 중복 체크
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 학번입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성 (권한성 필드는 엔티티 기본값 사용)
        User user = User.builder()
                .studentId(request.getStudentId())
                .department(request.getDepartment())
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .build();
        
        // 안전장치: reportCount 명시적 초기화
        user.setReportCount(0);

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    public TokenResponse login(UserLoginRequest request) {
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD, "비밀번호가 올바르지 않습니다.");
        }

        // 마지막 로그인 시간 업데이트
        user.setLastLoginAt(java.time.LocalDateTime.now());

        // TODO: JWT 토큰 생성 로직 구현 필요
        String token = "dummy-token-" + user.getId();
        
        return TokenResponse.builder()
                .accessToken(token)
                .expiresIn(3600L)
                .build();
    }

    @Transactional
    public void logout(String username) {
        // TODO: JWT 토큰 무효화 로직 구현 필요
        log.info("사용자 {} 로그아웃", username);
    }

    @Transactional
    public void deleteAccount(String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        
        // Soft delete
        user.setIsDeleted(true);
        userRepository.save(user);
        log.info("사용자 {} 계정 삭제", username);
    }

    public BadgeResponse checkBadge(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return BadgeResponse.from(user.getId(), user.getApprovalStatus(), user.isOperator());
    }

    public boolean verifyPassword(VerifyPasswordRequest request, String username) {
        User user = userRepository.findByStudentId(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash());
    }
}
