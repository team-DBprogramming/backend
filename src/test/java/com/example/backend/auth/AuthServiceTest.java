package com.example.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.studentUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.LogoutRequest;
import com.example.backend.dto.auth.AccessTokenResponse;
import com.example.backend.dto.auth.AuthUser;
import com.example.backend.mapper.AuthMapper;
import com.example.backend.mapper.RefreshTokenMapper;
import com.example.backend.service.AuthService;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenPair;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

  private final Clock clock = Clock.fixed(Instant.parse("2026-05-30T00:00:00Z"), ZoneOffset.UTC);
  private FakeAuthMapper authMapper;
  private FakeRefreshTokenMapper refreshTokenMapper;
  private JwtTokenProvider tokenProvider;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    authMapper = new FakeAuthMapper();
    refreshTokenMapper = new FakeRefreshTokenMapper();
    tokenProvider =
        new JwtTokenProvider(
            "test-secret-key-test-secret-key-test-secret-key",
            Duration.ofMinutes(30),
            Duration.ofDays(1),
            Duration.ofDays(30),
            clock);
    authService = new AuthService(authMapper, refreshTokenMapper, tokenProvider, clock);
  }

  @Test
  void loginAuthenticatesStudentWithPhoneLastFourDigits() {
    authMapper.save(
        new AuthUser(
            1L, "2024123456", "STUDENT", "010-1234-5678", 1, "Student One", "computer-science"));

    LoginResponse response = authService.login(new LoginRequest("2024123456", "5678", false));

    assertThat(response.accessToken()).isNotBlank();
    assertThat(response.refreshToken()).isNotBlank();
    assertThat(response.user().id()).isEqualTo("u_1");
    assertThat(response.user().userId()).isEqualTo("2024123456");
    assertThat(response.user().name()).isEqualTo("Student One");
    assertThat(response.user().role()).isEqualTo("student");
    assertThat(response.user().department()).isEqualTo("computer-science");
    assertThat(refreshTokenMapper.lastRememberMe).isZero();
    assertThat(refreshTokenMapper.lastExpiresAt).isEqualTo(Instant.parse("2026-05-31T00:00:00Z"));
  }

  @Test
  void loginUsesLongRefreshTokenExpiryWhenRememberMeIsTrue() {
    authMapper.save(
        new AuthUser(
            2L, "P1001", "PROFESSOR", "01099991234", 1, "Professor One", "computer-science"));

    LoginResponse response = authService.login(new LoginRequest("P1001", "1234", true));

    assertThat(response.user().role()).isEqualTo("professor");
    assertThat(refreshTokenMapper.lastRememberMe).isOne();
    assertThat(refreshTokenMapper.lastExpiresAt).isEqualTo(Instant.parse("2026-06-29T00:00:00Z"));
  }

  @Test
  void loginRejectsMissingUserIdOrPassword() {
    assertThatThrownBy(() -> authService.login(new LoginRequest("", "1234", false)))
        .isInstanceOfSatisfying(
            AuthHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("AUTH4001"));
  }

  @Test
  void loginRejectsInvalidCredentials() {
    authMapper.save(
        new AuthUser(
            1L, "2024123456", "STUDENT", "010-1234-5678", 1, "Student One", "computer-science"));

    assertThatThrownBy(() -> authService.login(new LoginRequest("2024123456", "0000", false)))
        .isInstanceOfSatisfying(
            AuthHandler.class,
            exception -> {
              assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("AUTH4011");
              assertThat(exception.getMessage()).isEqualTo(ErrorStatus.AUTH_INVALID_CREDENTIALS.getMessage());
            });
  }

  @Test
  void logoutRevokesRefreshToken() {
    TokenPair tokens = tokenProvider.createTokenPair(1L, "2024123456", "STUDENT", false);
    String refreshTokenHash = tokenProvider.hashToken(tokens.refreshToken());
    refreshTokenMapper.saveActiveToken(refreshTokenHash, tokens.refreshTokenExpiresAt());

    authService.logout(studentUser(), new LogoutRequest(tokens.refreshToken()));

    assertThat(refreshTokenMapper.revokedTokenHash).isEqualTo(refreshTokenHash);
    assertThat(refreshTokenMapper.revokedLoginId).isEqualTo("2024123456");
    assertThat(refreshTokenMapper.revokedLoginType).isEqualTo("STUDENT");
  }

  @Test
  void logoutRejectsMissingRefreshToken() {
    TokenPair tokens = tokenProvider.createTokenPair(1L, "2024123456", "STUDENT", false);

    assertThatThrownBy(() -> authService.logout(studentUser(), new LogoutRequest("")))
        .isInstanceOfSatisfying(
            AuthHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("AUTH4002"));
  }

  @Test
  void reissueAccessTokenCreatesNewAccessTokenFromActiveRefreshToken() {
    TokenPair tokens = tokenProvider.createTokenPair(1L, "2024123456", "STUDENT", false);
    refreshTokenMapper.saveActiveToken(
        tokenProvider.hashToken(tokens.refreshToken()), tokens.refreshTokenExpiresAt());

    AccessTokenResponse response = authService.reissueAccessToken(new LogoutRequest(tokens.refreshToken()));

    assertThat(response.accessToken()).isNotBlank();
    assertThat(response.accessTokenExpiresAt()).isEqualTo(Instant.parse("2026-05-30T00:30:00Z"));
  }

  @Test
  void reissueAccessTokenRejectsRevokedOrUnknownRefreshToken() {
    TokenPair tokens = tokenProvider.createTokenPair(1L, "2024123456", "STUDENT", false);

    assertThatThrownBy(() -> authService.reissueAccessToken(new LogoutRequest(tokens.refreshToken())))
        .isInstanceOfSatisfying(
            AuthHandler.class,
            exception ->
                assertThat(exception.getErrorReasonHttpStatus().getCode()).isEqualTo("AUTH4012"));
  }

  private static class FakeAuthMapper implements AuthMapper {
    private final Map<String, AuthUser> users = new HashMap<>();

    void save(AuthUser user) {
      users.put(user.loginId(), user);
    }

    @Override
    public void callAuthenticateLogin(Map<String, Object> params) {
      String loginId = (String) params.get("loginId");
      String password = (String) params.get("password");
      AuthUser user = users.get(loginId);
      if (user == null || !user.isActive()) {
        params.put("result", "INVALID_CREDENTIALS");
        return;
      }
      if (!"STUDENT".equals(user.role()) && !"PROFESSOR".equals(user.role())) {
        params.put("result", "MISSING_ROLE");
        return;
      }
      if (!matchesPassword(password, user)) {
        params.put("result", "INVALID_CREDENTIALS");
        return;
      }
      params.put("result", "LOGIN_SUCCESS");
      params.put("userId", user.userId());
      params.put("accountLoginId", user.loginId());
      params.put("role", user.role());
      params.put("roleDisplay", user.role().toLowerCase(java.util.Locale.ROOT));
      params.put("publicUserId", "u_" + user.userId());
      params.put("userName", user.name());
      params.put("department", user.department());
    }

    private boolean matchesPassword(String password, AuthUser user) {
      if (password == null) {
        return false;
      }
      if (user.passwordHash() != null && !user.passwordHash().trim().isEmpty()
          && user.passwordHash().equals(password.trim())) {
        return true;
      }
      String digits = user.phone() == null ? "" : user.phone().replaceAll("[^0-9]", "");
      return digits.length() >= 4 && digits.substring(digits.length() - 4).equals(password.trim());
    }
  }

  private static class FakeRefreshTokenMapper implements RefreshTokenMapper {
    private final Map<String, Instant> activeTokens = new HashMap<>();
    private int lastRememberMe;
    private Instant lastExpiresAt;
    private String revokedTokenHash;
    private String revokedLoginId;
    private String revokedLoginType;

    void saveActiveToken(String tokenHash, Instant expiresAt) {
      activeTokens.put(tokenHash, expiresAt);
    }

    @Override
    public void callSaveLoginSuccess(Map<String, Object> params) {
      lastRememberMe = ((Number) params.get("rememberMe")).intValue();
      lastExpiresAt = (Instant) params.get("expiresAt");
      activeTokens.put((String) params.get("tokenHash"), lastExpiresAt);
      params.put("result", "SAVE_SUCCESS");
    }

    @Override
    public int countActive(String tokenHash, Instant now) {
      Instant expiresAt = activeTokens.get(tokenHash);
      return expiresAt != null && expiresAt.isAfter(now) ? 1 : 0;
    }

    @Override
    public void callRevokeRefreshToken(Map<String, Object> params) {
      String tokenHash = (String) params.get("tokenHash");
      Instant revokedAt = (Instant) params.get("revokedAt");
      Instant expiresAt = activeTokens.get(tokenHash);
      if (expiresAt == null || !expiresAt.isAfter(revokedAt)) {
        params.put("result", "INVALID_TOKEN");
        return;
      }
      revokedTokenHash = tokenHash;
      revokedLoginId = (String) params.get("loginId");
      revokedLoginType = (String) params.get("loginType");
      activeTokens.remove(tokenHash);
      params.put("result", "REVOKE_SUCCESS");
    }
  }
}

