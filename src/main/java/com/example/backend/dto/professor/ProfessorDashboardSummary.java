package com.example.backend.dto.professor;

public class ProfessorDashboardSummary {

  private Integer courseCount;
  private Integer totalStudents;
  private Integer totalCapacity;
  private Double avgSatisfaction;
  private Integer newReviewCount;

  public ProfessorDashboardSummary() {}

  public ProfessorDashboardSummary(
      Integer courseCount,
      Integer totalStudents,
      Integer totalCapacity,
      Double avgSatisfaction,
      Integer newReviewCount) {
    this.courseCount = courseCount;
    this.totalStudents = totalStudents;
    this.totalCapacity = totalCapacity;
    this.avgSatisfaction = avgSatisfaction;
    this.newReviewCount = newReviewCount;
  }

  public Integer getCourseCount() {
    return courseCount;
  }

  public void setCourseCount(Integer courseCount) {
    this.courseCount = courseCount;
  }

  public Integer getTotalStudents() {
    return totalStudents;
  }

  public void setTotalStudents(Integer totalStudents) {
    this.totalStudents = totalStudents;
  }

  public Integer getTotalCapacity() {
    return totalCapacity;
  }

  public void setTotalCapacity(Integer totalCapacity) {
    this.totalCapacity = totalCapacity;
  }

  public Double getAvgSatisfaction() {
    return avgSatisfaction;
  }

  public void setAvgSatisfaction(Double avgSatisfaction) {
    this.avgSatisfaction = avgSatisfaction;
  }

  public Integer getNewReviewCount() {
    return newReviewCount;
  }

  public void setNewReviewCount(Integer newReviewCount) {
    this.newReviewCount = newReviewCount;
  }
}
