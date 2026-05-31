package com.example.backend.support;

import com.example.backend.security.AuthenticatedUser;
import com.example.backend.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestAuthentications {

  private TestAuthentications() {}

  public static AuthenticatedUser professorUser() {
    return new AuthenticatedUser(10L, "P1001", "PROFESSOR");
  }

  public static AuthenticatedUser studentUser() {
    return new AuthenticatedUser(1L, "2024123456", "STUDENT");
  }

  public static Authentication professorAuthentication() {
    return new UsernamePasswordAuthenticationToken(professorPrincipal(), null);
  }

  public static Authentication studentAuthentication() {
    return new UsernamePasswordAuthenticationToken(studentPrincipal(), null);
  }

  public static RequestPostProcessor withProfessorAuthentication() {
    return withAuthentication(professorAuthentication());
  }

  public static RequestPostProcessor withStudentAuthentication() {
    return withAuthentication(studentAuthentication());
  }

  private static RequestPostProcessor withAuthentication(Authentication authentication) {
    return request -> {
      SecurityContextHolder.getContext().setAuthentication(authentication);
      request.setUserPrincipal(authentication);
      return request;
    };
  }

  private static CustomUserDetails professorPrincipal() {
    return CustomUserDetails.from(professorUser());
  }

  private static CustomUserDetails studentPrincipal() {
    return CustomUserDetails.from(studentUser());
  }
}
