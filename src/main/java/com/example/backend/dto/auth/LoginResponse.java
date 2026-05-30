package com.example.backend.dto.auth;

import java.time.Instant;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt,
    UserSummary user) {

  public record UserSummary(
      String id, String userId, String name, String role, String department) {}
}
