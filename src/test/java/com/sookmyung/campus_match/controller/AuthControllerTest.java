package com.sookmyung.campus_match.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sookmyung.campus_match.dto.auth.UserRegisterRequest;
import com.sookmyung.campus_match.dto.user.UserResponse;
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
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void testUserRegistration() throws Exception {
        // Given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .studentId("20240001")
                .password("Password123!")  // 영문, 숫자, 특수문자 포함
                .name("김철수")
                .department("컴퓨터학부")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("010-1234-5678")
                .email("test@sookmyung.ac.kr")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .studentId("20240001")
                .name("김철수")
                .department("컴퓨터학부")
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        when(authService.register(any(UserRegisterRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value("20240001"))
                .andExpect(jsonPath("$.data.name").value("김철수"));
    }
}
