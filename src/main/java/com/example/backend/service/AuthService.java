package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.dto.auth.AccessTokenResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LoginResponse.UserSummary;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.mapper.AuthMapper;
import com.example.backend.mapper.RefreshTokenMapper;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.utils.AccessToken;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenClaims;
import com.example.backend.utils.TokenPair;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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

    Map<String, Object> authParams = new HashMap<>();
    authParams.put("loginId", request.userId().trim());
    authParams.put("password", request.password().trim());
    authMapper.callAuthenticateLogin(authParams);
    ensureLoginSuccess(authParams);

    boolean rememberMe = request.rememberMeOrFalse();
    Long userId = longValue(authParams.get("userId"));
    String loginId = stringValue(authParams.get("accountLoginId"));
    String role = stringValue(authParams.get("role"));
    TokenPair tokens = tokenProvider.createTokenPair(userId, loginId, role, rememberMe);
    Map<String, Object> saveParams = new HashMap<>();
    saveParams.put("userId", userId);
    saveParams.put("tokenHash", tokenProvider.hashToken(tokens.refreshToken()));
    saveParams.put("rememberMe", rememberMe ? 1 : 0);
    saveParams.put("expiresAt", tokens.refreshTokenExpiresAt());
    refreshTokenMapper.callSaveLoginSuccess(saveParams);
    ensureSaveSuccess(saveParams);

    return new LoginResponse(
        tokens.accessToken(),
        tokens.refreshToken(),
        tokens.accessTokenExpiresAt(),
        tokens.refreshTokenExpiresAt(),
        new UserSummary(
            stringValue(authParams.get("publicUserId")),
            loginId,
            stringValue(authParams.get("userName")),
            stringValue(authParams.get("roleDisplay")),
            stringValue(authParams.get("department"))));
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
    Map<String, Object> revokeParams = new HashMap<>();
    revokeParams.put("tokenHash", tokenHash);
    revokeParams.put("loginId", refreshClaims.loginId());
    revokeParams.put("loginType", refreshClaims.role());
    revokeParams.put("revokedAt", clock.instant());
    refreshTokenMapper.callRevokeRefreshToken(revokeParams);
    ensureRevokeSuccess(revokeParams);
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

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void ensureLoginSuccess(Map<String, Object> params) {
    String result = stringValue(params.get("result"));
    if ("LOGIN_SUCCESS".equals(result)) {
      return;
    }
    if ("MISSING_CREDENTIALS".equals(result)) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_CREDENTIALS);
    }
    if ("MISSING_ROLE".equals(result)) {
      throw new AuthHandler(ErrorStatus.AUTH_MISSING_ROLE);
    }
    throw new AuthHandler(ErrorStatus.AUTH_INVALID_CREDENTIALS);
  }

  private void ensureSaveSuccess(Map<String, Object> params) {
    if (!"SAVE_SUCCESS".equals(stringValue(params.get("result")))) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_CREDENTIALS);
    }
  }

  private void ensureRevokeSuccess(Map<String, Object> params) {
    if (!"REVOKE_SUCCESS".equals(stringValue(params.get("result")))) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
  }

  private Long longValue(Object value) {
    if (value instanceof Number number) {
      return number.longValue();
    }
    return Long.valueOf(String.valueOf(value));
  }

  private String stringValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }
}
