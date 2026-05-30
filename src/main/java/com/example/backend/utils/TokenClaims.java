package com.example.backend.utils;

public record TokenClaims(Long userId, String loginId, String role) {}
