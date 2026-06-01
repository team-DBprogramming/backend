package com.example.backend.dto.student;

public class StudentCreditSummary {

  private Integer applied;
  private Integer max;
  private Integer courseCount;
  private Integer cartCount;

  public StudentCreditSummary() {}

  public StudentCreditSummary(Integer applied, Integer max, Integer courseCount, Integer cartCount) {
    this.applied = applied;
    this.max = max;
    this.courseCount = courseCount;
    this.cartCount = cartCount;
  }

  public Integer getApplied() {
    return applied;
  }

  public void setApplied(Integer applied) {
    this.applied = applied;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public Integer getCourseCount() {
    return courseCount;
  }

  public void setCourseCount(Integer courseCount) {
    this.courseCount = courseCount;
  }

  public Integer getCartCount() {
    return cartCount;
  }

  public void setCartCount(Integer cartCount) {
    this.cartCount = cartCount;
  }
}
