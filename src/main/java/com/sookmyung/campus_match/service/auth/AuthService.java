package com.sookmyung.campus_match.service.auth;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.user.Profile;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;

import com.sookmyung.campus_match.dto.auth.BadgeResponse;
import com.sookmyung.campus_match.dto.auth.TokenResponse;
import com.sookmyung.campus_match.dto.auth.UserLoginRequest;
import com.sookmyung.campus_match.dto.auth.UserRegisterRequest;
import com.sookmyung.campus_match.dto.user.UserResponse;
import com.sookmyung.campus_match.dto.auth.VerifyPasswordRequest;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.repository.user.ProfileRepository;
import com.sookmyung.campus_match.config.security.jwt.JwtTokenProvider;
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
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다.");
        }

        // 학번 중복 확인
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new ApiException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 학번입니다.");
        }

        // 비밀번호 해시화
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .studentId(request.getStudentId())
                .department(request.getDepartment())
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .approvalStatus(ApprovalStatus.PENDING)
                .operator(false)
                .build();

        User savedUser = userRepository.save(user);
        
        // 기본 프로필 생성
        Profile profile = Profile.builder()
                .user(savedUser)
                .department(savedUser.getDepartment())
                .studentCode(savedUser.getStudentId())
                .headline("새로운 사용자")
                .bio("안녕하세요!")
                .greetingEnabled(true)
                .viewCount(0)
                .build();
        
        profileRepository.save(profile);
        
        return UserResponse.from(savedUser);
    }

    public TokenResponse login(UserLoginRequest request) {
        log.info("로그인 시도 - studentId: {}", request.getStudentId());
        
        User user = userRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD, "비밀번호가 올바르지 않습니다.");
        }

        // 마지막 로그인 시간 업데이트
        user.setLastLoginAt(java.time.LocalDateTime.now());

        // JWT 토큰 생성
        log.info("JWT 토큰 생성 시작 - userId: {}", user.getId());
        String token = jwtTokenProvider.generateToken(user.getId().toString());
        log.info("JWT 토큰 생성 완료 - token: {}", token);
        
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
