package com.example.backend.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static com.example.backend.support.TestAuthentications.studentUser;
import static com.example.backend.support.TestAuthentications.withStudentAuthentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.AuthController;
import com.example.backend.dto.auth.AccessTokenResponse;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LoginResponse.UserSummary;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.service.AuthService;
import com.example.backend.utils.JwtTokenProvider;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthService authService;
  @MockitoBean private JwtTokenProvider tokenProvider;

  @Test
  void loginReturnsProjectApiResponseFormat() throws Exception {
    when(authService.login(any()))
        .thenReturn(
            new LoginResponse(
                "access-token",
                "refresh-token",
                Instant.parse("2026-05-30T00:30:00Z"),
                Instant.parse("2026-06-29T00:00:00Z"),
                new UserSummary("u_1", "2024123456", "Student One", "student", "computer-science")));

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "userId": "2024123456",
                      "password": "5678",
                      "rememberMe": true
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("AUTH200"))
        .andExpect(jsonPath("$.message").value("로그인 성공"))
        .andExpect(jsonPath("$.result.accessToken").value("access-token"))
        .andExpect(jsonPath("$.result.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.result.user.role").value("student"));
  }

  @Test
  void logoutReturnsProjectApiResponseFormat() throws Exception {
    doNothing().when(authService).logout(eq(studentUser()), any(LogoutRequest.class));

    mockMvc
        .perform(
            post("/auth/logout")
                .with(withStudentAuthentication())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "refreshToken": "refresh-token"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("AUTH200"))
        .andExpect(jsonPath("$.message").value("로그아웃 성공"));
  }

  @Test
  void reissueReturnsProjectApiResponseFormat() throws Exception {
    when(authService.reissueAccessToken(any()))
        .thenReturn(new AccessTokenResponse("new-access-token", Instant.parse("2026-05-30T00:30:00Z")));

    mockMvc
        .perform(
            post("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "refreshToken": "refresh-token"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("AUTH200"))
        .andExpect(jsonPath("$.message").value("Access Token 재발급 성공"))
        .andExpect(jsonPath("$.result.accessToken").value("new-access-token"));
  }
}
