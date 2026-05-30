package com.example.backend.controller;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.SuccessStatus;
import com.example.backend.dto.auth.AccessTokenResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    return ApiResponse.of(SuccessStatus.AUTH_LOGIN, authService.login(request));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(
      @RequestHeader("Authorization") String authorization,
      @RequestBody LogoutRequest request) {
    authService.logout(authorization, request);
    return ApiResponse.of(SuccessStatus.AUTH_LOGOUT, null);
  }

  @PostMapping("/token/refresh")
  public ApiResponse<AccessTokenResponse> reissue(@RequestBody LogoutRequest request) {
    return ApiResponse.of(SuccessStatus.AUTH_REISSUE, authService.reissueAccessToken(request));
  }
}
