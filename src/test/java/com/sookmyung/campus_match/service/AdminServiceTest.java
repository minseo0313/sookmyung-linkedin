package com.sookmyung.campus_match.service;

import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.admin.ApproveUserRequest;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.user.UserRepository;
import com.sookmyung.campus_match.service.admin.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private ApproveUserRequest approveRequest;
    private ApproveUserRequest rejectRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .studentId("20240001")
                .name("테스트 사용자")
                .email("test@sookmyung.ac.kr")
                .department("컴퓨터학부")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("01012345678")
                .password("encodedPassword")
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        approveRequest = ApproveUserRequest.builder()
                .approved(true)
                .build();

        rejectRequest = ApproveUserRequest.builder()
                .approved(false)
                .build();
    }

    @Test
    void approveUser_WhenUserExists_ShouldApproveUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        adminService.approveUser(1L, approveRequest);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        assertEquals(ApprovalStatus.APPROVED, testUser.getApprovalStatus());
    }

    @Test
    void approveUser_WhenUserExists_ShouldRejectUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        adminService.approveUser(1L, rejectRequest);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        assertEquals(ApprovalStatus.REJECTED, testUser.getApprovalStatus());
    }

    @Test
    void approveUser_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            adminService.approveUser(1L, approveRequest);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserRole_WhenUserExists_ShouldChangeOperatorStatus() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        adminService.changeUserRole(1L, true);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        assertTrue(testUser.isOperator());
    }

    @Test
    void changeUserRole_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            adminService.changeUserRole(1L, true);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
