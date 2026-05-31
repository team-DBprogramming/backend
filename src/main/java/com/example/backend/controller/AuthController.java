package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.config.OpenApiExamples;
import com.example.backend.dto.auth.AccessTokenResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "학번/교번 기반 로그인, 로그아웃, 토큰 재발급 API")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  @Operation(
      summary = "로그인",
      description = "학생은 학번, 교수는 P + 교수 번호로 로그인합니다. 비밀번호는 전화번호 뒷자리 4자리입니다.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "로그인 요청",
              content =
                  @Content(
                      mediaType = "application/json",
                      examples = @ExampleObject(value = OpenApiExamples.LOGIN_REQUEST))))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "로그인 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.LOGIN_RESPONSE)))
  public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    return ApiResponse.of(SuccessStatus.AUTH_LOGIN, authService.login(request));
  }

  @PostMapping("/logout")
  @Operation(
      summary = "로그아웃",
      description = "현재 로그인된 사용자의 Refresh Token을 무효화합니다.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "로그아웃 요청",
              content =
                  @Content(
                      mediaType = "application/json",
                      examples = @ExampleObject(value = OpenApiExamples.LOGOUT_REQUEST))))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "로그아웃 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.LOGOUT_RESPONSE)))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "인증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.ERROR_RESPONSE)))
  public ApiResponse<Void> logout(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody LogoutRequest request) {
    authService.logout(userDetails.toAuthenticatedUser(), request);
    return ApiResponse.of(SuccessStatus.AUTH_LOGOUT, null);
  }

  @PostMapping("/token/refresh")
  @Operation(
      summary = "Access Token 재발급",
      description = "유효한 Refresh Token으로 새 Access Token을 발급합니다.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "토큰 재발급 요청",
              content =
                  @Content(
                      mediaType = "application/json",
                      examples = @ExampleObject(value = OpenApiExamples.LOGOUT_REQUEST))))
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Access Token 재발급 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = OpenApiExamples.TOKEN_REFRESH_RESPONSE)))
  public ApiResponse<AccessTokenResponse> reissue(@RequestBody LogoutRequest request) {
    return ApiResponse.of(SuccessStatus.AUTH_REISSUE, authService.reissueAccessToken(request));
  }
}
