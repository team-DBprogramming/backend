package com.example.backend.dto.student;

public class StudentQuickActions {

  private Integer cartCount;
  private Integer reviewPendingCount;

  public StudentQuickActions() {}

  public StudentQuickActions(Integer cartCount, Integer reviewPendingCount) {
    this.cartCount = cartCount;
    this.reviewPendingCount = reviewPendingCount;
  }

  public Integer getCartCount() {
    return cartCount;
  }

  public void setCartCount(Integer cartCount) {
    this.cartCount = cartCount;
  }

  public Integer getReviewPendingCount() {
    return reviewPendingCount;
  }

  public void setReviewPendingCount(Integer reviewPendingCount) {
    this.reviewPendingCount = reviewPendingCount;
  }
}
