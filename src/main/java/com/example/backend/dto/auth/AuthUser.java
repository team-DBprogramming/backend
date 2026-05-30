package com.example.backend.dto.auth;

public class AuthUser {

  private Long userId;
  private String loginId;
  private String role;
  private String phone;
  private Integer active;
  private String name;
  private String department;

  public AuthUser() {}

  public AuthUser(
      Long userId,
      String loginId,
      String role,
      String phone,
      Integer active,
      String name,
      String department) {
    this.userId = userId;
    this.loginId = loginId;
    this.role = role;
    this.phone = phone;
    this.active = active;
    this.name = name;
    this.department = department;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long userId() {
    return userId;
  }

  public String getLoginId() {
    return loginId;
  }

  public void setLoginId(String loginId) {
    this.loginId = loginId;
  }

  public String loginId() {
    return loginId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String role() {
    return role;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String phone() {
    return phone;
  }

  public Integer getActive() {
    return active;
  }

  public void setActive(Integer active) {
    this.active = active;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String department() {
    return department;
  }

  public boolean isActive() {
    return Integer.valueOf(1).equals(active);
  }
}
