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
    refreshTokenMapper.insert(1L, refreshTokenHash, 0, tokens.refreshTokenExpiresAt());

    authService.logout(studentUser(), new LogoutRequest(tokens.refreshToken()));

    assertThat(refreshTokenMapper.revokedTokenHash).isEqualTo(refreshTokenHash);
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
    refreshTokenMapper.insert(
        1L, tokenProvider.hashToken(tokens.refreshToken()), 0, tokens.refreshTokenExpiresAt());

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
    public AuthUser findByLoginId(String loginId) {
      return users.get(loginId);
    }

    @Override
    public void updateLastLoginAt(Long userId, Instant lastLoginAt) {}
  }

  private static class FakeRefreshTokenMapper implements RefreshTokenMapper {
    private final Map<String, Instant> activeTokens = new HashMap<>();
    private int lastRememberMe;
    private Instant lastExpiresAt;
    private String revokedTokenHash;

    @Override
    public void insert(Long userId, String tokenHash, int rememberMe, Instant expiresAt) {
      lastRememberMe = rememberMe;
      lastExpiresAt = expiresAt;
      activeTokens.put(tokenHash, expiresAt);
    }

    @Override
    public int countActive(String tokenHash, Instant now) {
      Instant expiresAt = activeTokens.get(tokenHash);
      return expiresAt != null && expiresAt.isAfter(now) ? 1 : 0;
    }

    @Override
    public void revoke(String tokenHash, Instant revokedAt) {
      revokedTokenHash = tokenHash;
      activeTokens.remove(tokenHash);
    }
  }
}

