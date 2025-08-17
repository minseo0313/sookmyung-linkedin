package com.sookmyung.campus_match.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sookmyung.campus_match.dto.auth.UserRegisterRequest;
import com.sookmyung.campus_match.dto.auth.UserResponse;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import com.sookmyung.campus_match.service.auth.AuthService;
import com.sookmyung.campus_match.controller.auth.AuthController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 검증 에러 응답에서 특정 필드의 에러가 존재하는지 확인하는 헬퍼 메서드
     */
    private void assertFieldErrorExists(String fieldName) throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors[*].field").value(org.hamcrest.Matchers.hasItem(fieldName)));
    }

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_WhenValidRequest_ShouldReturn201() throws Exception {
        // Given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .name("홍길동")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("01012345678")
                .studentId("20240001")
                .department("컴퓨터학부")
                .email("test@sookmyung.ac.kr")
                .password("password123!")
                .build();

        // 성공 응답을 위한 UserResponse 생성
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("20240001")
                .studentId("20240001")
                .sookmyungEmail("test@sookmyung.ac.kr")
                .fullName("홍길동")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phone("01012345678")
                .department("컴퓨터학부")
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        // AuthService.register 메서드가 성공 응답을 반환하도록 목킹
        when(authService.register(any(UserRegisterRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/users/1")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.fullName").value("홍길동"))
                .andExpect(jsonPath("$.data.studentId").value("20240001"));
    }

    @Test
    void register_WhenNameIsMissing_ShouldReturn400WithFieldError() throws Exception {
        // Given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("01012345678")
                .studentId("20240001")
                .department("컴퓨터학부")
                .email("test@sookmyung.ac.kr")
                .password("password123!")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.errors[*].field").value(org.hamcrest.Matchers.hasItem("name")))
                .andExpect(jsonPath("$.data.errors[?(@.field == 'name')].message").exists());
    }

    @Test
    void register_WhenEmailIsInvalid_ShouldReturn400WithFieldError() throws Exception {
        // Given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .name("홍길동")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("01012345678")
                .studentId("20240001")
                .department("컴퓨터학부")
                .email("invalid-email")
                .password("password123!")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.errors[*].field").value(org.hamcrest.Matchers.hasItem("email")))
                .andExpect(jsonPath("$.data.errors[?(@.field == 'email')].message").exists());
    }

    @Test
    void register_WhenPasswordIsTooShort_ShouldReturn400WithFieldError() throws Exception {
        // Given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .name("홍길동")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("01012345678")
                .studentId("20240001")
                .department("컴퓨터학부")
                .email("test@sookmyung.ac.kr")
                .password("123")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.errors[*].field").value(org.hamcrest.Matchers.hasItem("password")))
                .andExpect(jsonPath("$.data.errors[?(@.field == 'password')].message").exists());
    }

    @Test
    void register_WhenMultipleFieldsAreInvalid_ShouldReturn400WithAllFieldErrors() throws Exception {
        // Given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .name("") // 빈 문자열
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("123") // 너무 짧음
                .studentId("") // 빈 문자열
                .department("컴퓨터학부")
                .email("invalid-email")
                .password("123") // 너무 짧음
                .build();

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.data.errors[*].field").value(org.hamcrest.Matchers.hasItems("name", "phoneNumber", "studentId", "email", "password")))
                .andExpect(jsonPath("$.data.errors[*].message").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.notNullValue())));
    }
}
