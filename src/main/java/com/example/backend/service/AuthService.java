package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LoginResponse.UserSummary;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.dto.auth.AuthUser;
import com.example.backend.mapper.AuthMapper;
import com.example.backend.mapper.RefreshTokenMapper;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenPair;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final AuthMapper authMapper;
  private final RefreshTokenMapper refreshTokenMapper;
  private final JwtTokenProvider tokenProvider;
  private final Clock clock;

  public AuthService(
      AuthMapper authMapper,
      RefreshTokenMapper refreshTokenMapper,
      JwtTokenProvider tokenProvider,
      Clock clock) {
    this.authMapper = authMapper;
    this.refreshTokenMapper = refreshTokenMapper;
    this.tokenProvider = tokenProvider;
    this.clock = clock;
  }

  @Transactional
  public LoginResponse login(LoginRequest request) {
    if (request == null || isBlank(request.userId()) || isBlank(request.password())) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_CREDENTIALS);
    }

    AuthUser user = authMapper.findByLoginId(request.userId().trim());
    if (user == null || !user.isActive()) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_CREDENTIALS);
    }

    if (!isStudentOrProfessor(user.role())) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_ROLE);
    }

    if (!matchesPhoneLastFourDigits(request.password(), user.phone())) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_CREDENTIALS);
    }

    boolean rememberMe = request.rememberMeOrFalse();
    TokenPair tokens = tokenProvider.createTokenPair(user.userId(), user.loginId(), user.role(), rememberMe);
    refreshTokenMapper.insert(
        user.userId(),
        tokenProvider.hashToken(tokens.refreshToken()),
        rememberMe ? 1 : 0,
        tokens.refreshTokenExpiresAt());
    authMapper.updateLastLoginAt(user.userId(), clock.instant());

    return new LoginResponse(
        tokens.accessToken(),
        tokens.refreshToken(),
        tokens.accessTokenExpiresAt(),
        tokens.refreshTokenExpiresAt(),
        new UserSummary(
            "u_" + user.userId(),
            user.loginId(),
            user.name(),
            user.role().toLowerCase(Locale.ROOT),
            user.department()));
  }

  @Transactional
  public void logout(String authorizationHeader, LogoutRequest request) {
    if (isBlank(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    if (request == null || isBlank(request.refreshToken())) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_REFRESH_TOKEN);
    }

    tokenProvider.validateAccessToken(authorizationHeader.substring("Bearer ".length()).trim());
    tokenProvider.validateRefreshToken(request.refreshToken());

    String tokenHash = tokenProvider.hashToken(request.refreshToken());
    Instant now = clock.instant();
    if (refreshTokenMapper.countActive(tokenHash, now) <= 0) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    refreshTokenMapper.revoke(tokenHash, now);
  }

  private boolean matchesPhoneLastFourDigits(String password, String phone) {
    String digits = phone == null ? "" : phone.replaceAll("[^0-9]", "");
    return digits.length() >= 4 && digits.substring(digits.length() - 4).equals(password.trim());
  }

  private boolean isStudentOrProfessor(String role) {
    return "STUDENT".equals(role) || "PROFESSOR".equals(role);
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
