package com.example.backend.utils;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

  private final String secret;
  private final Duration accessTokenTtl;
  private final Duration refreshTokenTtl;
  private final Duration rememberMeRefreshTokenTtl;
  private final Clock clock;

  public JwtTokenProvider(
      @Value("${auth.jwt.secret}") String secret,
      @Value("${auth.jwt.access-token-ttl}") Duration accessTokenTtl,
      @Value("${auth.jwt.refresh-token-ttl}") Duration refreshTokenTtl,
      @Value("${auth.jwt.remember-me-refresh-token-ttl}") Duration rememberMeRefreshTokenTtl,
      Clock clock) {
    this.secret = secret;
    this.accessTokenTtl = accessTokenTtl;
    this.refreshTokenTtl = refreshTokenTtl;
    this.rememberMeRefreshTokenTtl = rememberMeRefreshTokenTtl;
    this.clock = clock;
  }

  public TokenPair createTokenPair(Long userId, String loginId, String role, boolean rememberMe) {
    Instant now = clock.instant();
    Instant accessExpiresAt = now.plus(accessTokenTtl);
    Instant refreshExpiresAt = now.plus(rememberMe ? rememberMeRefreshTokenTtl : refreshTokenTtl);

    return new TokenPair(
        createToken(userId, loginId, role, "access", now, accessExpiresAt),
        createToken(userId, loginId, role, "refresh", now, refreshExpiresAt),
        accessExpiresAt,
        refreshExpiresAt);
  }

  public AccessToken createAccessToken(Long userId, String loginId, String role) {
    Instant now = clock.instant();
    Instant accessExpiresAt = now.plus(accessTokenTtl);
    return new AccessToken(
        createToken(userId, loginId, role, "access", now, accessExpiresAt), accessExpiresAt);
  }

  public TokenClaims validateAccessToken(String token) {
    Map<String, Object> claims = parseAndValidate(token);
    if (!"access".equals(claims.get("typ"))) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    return toTokenClaims(claims);
  }

  public TokenClaims validateRefreshToken(String token) {
    Map<String, Object> claims = parseAndValidate(token);
    if (!"refresh".equals(claims.get("typ"))) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    return toTokenClaims(claims);
  }

  public String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return BASE64_URL_ENCODER.encodeToString(hash);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to hash token", e);
    }
  }

  private String createToken(
      Long userId, String loginId, String role, String type, Instant issuedAt, Instant expiresAt) {
    Map<String, Object> header = new LinkedHashMap<>();
    header.put("alg", "HS256");
    header.put("typ", "JWT");

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("sub", String.valueOf(userId));
    payload.put("loginId", loginId);
    payload.put("role", role);
    payload.put("typ", type);
    payload.put("iat", issuedAt.getEpochSecond());
    payload.put("exp", expiresAt.getEpochSecond());

    String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
    return unsignedToken + "." + sign(unsignedToken);
  }

  private Map<String, Object> parseAndValidate(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
      }

      String unsignedToken = parts[0] + "." + parts[1];
      String expectedSignature = sign(unsignedToken);
      if (!MessageDigest.isEqual(
          expectedSignature.getBytes(StandardCharsets.UTF_8),
          parts[2].getBytes(StandardCharsets.UTF_8))) {
        throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
      }

      Map<String, Object> claims =
          OBJECT_MAPPER.readValue(
              BASE64_URL_DECODER.decode(parts[1]), new TypeReference<Map<String, Object>>() {});
      Number exp = (Number) claims.get("exp");
      if (exp == null || Instant.ofEpochSecond(exp.longValue()).isBefore(clock.instant())) {
        throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
      }
      return claims;
    } catch (AuthHandler e) {
      throw e;
    } catch (Exception e) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
  }

  private TokenClaims toTokenClaims(Map<String, Object> claims) {
    try {
      return new TokenClaims(
          Long.valueOf(String.valueOf(claims.get("sub"))),
          String.valueOf(claims.get("loginId")),
          String.valueOf(claims.get("role")));
    } catch (Exception e) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
  }

  private String encodeJson(Map<String, Object> value) {
    try {
      return BASE64_URL_ENCODER.encodeToString(OBJECT_MAPPER.writeValueAsBytes(value));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to encode JWT JSON", e);
    }
  }

  private String sign(String value) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to sign JWT", e);
    }
  }
}
