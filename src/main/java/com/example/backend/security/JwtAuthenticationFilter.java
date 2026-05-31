package com.example.backend.security;

import com.example.backend.apiPayload.ApiResponse;
import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.AuthHandler;
import com.example.backend.utils.JwtTokenProvider;
import com.example.backend.utils.TokenClaims;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, ObjectMapper objectMapper) {
    this.tokenProvider = tokenProvider;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || authorization.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!authorization.startsWith("Bearer ")) {
      writeAuthFailure(response);
      return;
    }

    try {
      TokenClaims claims = tokenProvider.validateAccessToken(authorization.substring("Bearer ".length()).trim());
      CustomUserDetails principal = new CustomUserDetails(claims.userId(), claims.loginId(), claims.role());
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (AuthHandler e) {
      SecurityContextHolder.clearContext();
      writeAuthFailure(response);
    }
  }

  private void writeAuthFailure(HttpServletResponse response) throws IOException {
    response.setStatus(ErrorStatus.AUTH_INVALID_TOKEN.getHttpStatus().value());
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response
        .getWriter()
        .write(
            objectMapper.writeValueAsString(
                ApiResponse.onFailure(
                    ErrorStatus.AUTH_INVALID_TOKEN.getCode(),
                    ErrorStatus.AUTH_INVALID_TOKEN.getMessage(),
                    null)));
  }
}
