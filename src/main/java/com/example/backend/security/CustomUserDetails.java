package com.example.backend.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String loginId;
  private final String role;

  public CustomUserDetails(Long userId, String loginId, String role) {
    this.userId = userId;
    this.loginId = loginId;
    this.role = role;
  }

  public static CustomUserDetails from(AuthenticatedUser user) {
    return new CustomUserDetails(user.userId(), user.loginId(), user.role());
  }

  public AuthenticatedUser toAuthenticatedUser() {
    return new AuthenticatedUser(userId, loginId, role);
  }

  public Long getUserId() {
    return userId;
  }

  public String getLoginId() {
    return loginId;
  }

  public String getRole() {
    return role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return loginId;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
