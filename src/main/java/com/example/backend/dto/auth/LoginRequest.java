package com.example.backend.dto.auth;

public record LoginRequest(String userId, String password, Boolean rememberMe) {

  public boolean rememberMeOrFalse() {
    return Boolean.TRUE.equals(rememberMe);
  }
}
