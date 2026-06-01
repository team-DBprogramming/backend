package com.example.backend.security;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.apiPayload.exception.handler.StudentHandler;
import org.springframework.security.core.Authentication;

public record AuthenticatedUser(Long userId, String loginId, String role) {

  public static AuthenticatedUser from(Authentication authentication) {
    if (authentication == null) {
      throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
    }
    if (authentication.getPrincipal() instanceof AuthenticatedUser user) {
      return user;
    }
    if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
      return userDetails.toAuthenticatedUser();
    }
    throw new AuthHandler(ErrorStatus.AUTH_INVALID_TOKEN);
  }

  public Long requireProfessorUserId() {
    if (!"PROFESSOR".equals(role)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_FORBIDDEN);
    }
    return userId;
  }

  public Long requireStudentUserId() {
    if (!"STUDENT".equals(role)) {
      throw new StudentHandler(ErrorStatus.STUDENT_FORBIDDEN);
    }
    return userId;
  }
}
