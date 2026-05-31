package com.example.backend.dto.auth;

import java.time.Instant;

public record AccessTokenResponse(String accessToken, Instant accessTokenExpiresAt) {}
