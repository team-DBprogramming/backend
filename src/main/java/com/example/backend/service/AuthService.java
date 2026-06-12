package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.dto.auth.AccessTokenResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LoginResponse.UserSummary;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.dto.auth.AuthUser;
import com.example.backend.mapper.AuthMapper;
import com.example.backend.mapper.RefreshTokenMapper;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.utils.AccessToken;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenClaims;
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

    if (!matchesPassword(request.password(), user)) {
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
  public void logout(AuthenticatedUser currentUser, LogoutRequest request) {
    if (request == null || isBlank(request.refreshToken())) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_REFRESH_TOKEN);
    }

    TokenClaims refreshClaims = tokenProvider.validateRefreshToken(request.refreshToken());
    if (!currentUser.userId().equals(refreshClaims.userId())) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }

    String tokenHash = tokenProvider.hashToken(request.refreshToken());
    Instant now = clock.instant();
    if (refreshTokenMapper.countActive(tokenHash, now) <= 0) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    refreshTokenMapper.revoke(tokenHash, now);
  }

  @Transactional(readOnly = true)
  public AccessTokenResponse reissueAccessToken(LogoutRequest request) {
    if (request == null || isBlank(request.refreshToken())) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_REFRESH_TOKEN);
    }

    TokenClaims claims = tokenProvider.validateRefreshToken(request.refreshToken());
    String tokenHash = tokenProvider.hashToken(request.refreshToken());
    if (refreshTokenMapper.countActive(tokenHash, clock.instant()) <= 0) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }

    AccessToken accessToken =
        tokenProvider.createAccessToken(claims.userId(), claims.loginId(), claims.role());
    return new AccessTokenResponse(accessToken.token(), accessToken.expiresAt());
  }

  private boolean matchesPhoneLastFourDigits(String password, String phone) {
    String digits = phone == null ? "" : phone.replaceAll("[^0-9]", "");
    return digits.length() >= 4 && digits.substring(digits.length() - 4).equals(password.trim());
  }

  private boolean matchesPassword(String password, AuthUser user) {
    if (password == null) {
      return false;
    }
    if (!isBlank(user.passwordHash()) && user.passwordHash().equals(password.trim())) {
      return true;
    }
    return matchesPhoneLastFourDigits(password, user.phone());
  }

  private boolean isStudentOrProfessor(String role) {
    return "STUDENT".equals(role) || "PROFESSOR".equals(role);
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
