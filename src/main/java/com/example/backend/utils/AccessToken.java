package com.example.backend.utils;

import java.time.Instant;

public record AccessToken(String token, Instant expiresAt) {}
